package br.com.sigec.controller;

import br.com.sigec.dao.ReiteracaoDAO;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Reiteracao;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.service.PedidoExameService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller da tela principal do SIGEC.
 *
 * Usa exclusivamente os métodos já existentes em PedidoExameService:
 *   - listarPendentes() -> List<PedidoExame> (já retorna apenas CADASTRADO/SOLICITADO)
 *   - concluirPedido(PedidoExame)
 *
 * Funcionalidades de manutenção completa do pedido ficam nos respectivos
 * controllers de tela modal.
 */
public class MainController implements Initializable {

    // ---------- topo ----------
    @FXML private TextField txtPesquisaMatricula;
    @FXML private Button btnPesquisar;
    @FXML private HBox alertBox;
    @FXML private Label lblAlertaReiteracoes;

    // ---------- tabela ----------
    @FXML private TableView<PedidoExame> tableViewPedidos;
    @FXML private TableColumn<PedidoExame, String> colMatricula;
    @FXML private TableColumn<PedidoExame, String> colNomeSentenciado;
    @FXML private TableColumn<PedidoExame, String> colNumeroProcesso;
    @FXML private TableColumn<PedidoExame, String> colDataSolicitacao;
    @FXML private TableColumn<PedidoExame, String> colStatus;

    // ---------- rodape ----------
    @FXML private Button btnNovoPedido;
    @FXML private Button btnAbrirPedido;
    @FXML private Button btnRegistrarEntrevista;
    @FXML private Button btnRegistrarReiteracao;
    @FXML private Button btnConcluirPedido;
    @FXML private Button btnAtualizar;
    @FXML private Label lblPedidosReiterados;

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Troque por injeção (construtor, ServiceLocator etc.) quando integrar de verdade
    private final PedidoExameService pedidoExameService = new PedidoExameService();
    private final ReiteracaoDAO reiteracaoDAO = new ReiteracaoDAO();

    // Guarda a última lista completa vinda do backend, para permitir
    // filtrar por matrícula em memória sem depender de um método novo no service
    private List<PedidoExame> ultimaListaCompleta = List.of();
    private Set<Integer> idsPedidosReiterados = new HashSet<>();
    private boolean filtroSomenteReiterados;

    private final ObservableList<PedidoExame> pedidos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColunas();
        configurarTabela();
        tableViewPedidos.setItems(pedidos);

        // Alerta de reiterações depende de um service que ainda não existe
        // (ex.: ReiteracaoService.listarPendentesPorPedido). Mantido oculto por ora.
        alertBox.setVisible(false);
        alertBox.setManaged(false);

        desabilitarAcoesSemSelecao();
        carregarPedidos();
    }

    private void configurarColunas() {
        colMatricula.setCellValueFactory(dado -> {
            var sentenciado = dado.getValue().getSentenciado();
            return new SimpleStringProperty(sentenciado == null ? "" : sentenciado.getMatricula());
        });

        colNomeSentenciado.setCellValueFactory(dado -> {
            var sentenciado = dado.getValue().getSentenciado();
            return new SimpleStringProperty(sentenciado == null ? "" : sentenciado.getNome());
        });

        colNumeroProcesso.setCellValueFactory(dado ->
                new SimpleStringProperty(dado.getValue().getNumeroProcesso()));

        colDataSolicitacao.setCellValueFactory(dado -> {
            var data = dado.getValue().getDataSolicitacao();
            return new SimpleStringProperty(data == null ? "" : data.format(DATA_BR));
        });

        colStatus.setCellValueFactory(dado -> {
            var status = dado.getValue().getStatus();
            return new SimpleStringProperty(status == null ? "" : status.toString());
        });

        // Habilita botoes de acao apenas quando ha um pedido selecionado
        tableViewPedidos.getSelectionModel().selectedItemProperty()
                .addListener((obs, antigo, novo) -> atualizarEstadoPedidoSelecionado(novo));
    }

    private void configurarTabela() {
        tableViewPedidos.setRowFactory(tabela -> {
            TableRow<PedidoExame> linha = new TableRow<>() {
                @Override
                protected void updateItem(PedidoExame pedido, boolean empty) {
                    super.updateItem(pedido, empty);
                    getStyleClass().remove("reiterated-row");
                    if (!empty && pedido != null && pedidoFoiReiterado(pedido)) {
                        getStyleClass().add("reiterated-row");
                    }
                }
            };

            linha.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2
                        && !linha.isEmpty()) {
                    tableViewPedidos.getSelectionModel().select(linha.getItem());
                    abrirPedidoSelecionado();
                }
            });

            return linha;
        });
    }

    private void desabilitarAcoesSemSelecao() {
        atualizarEstadoBotoes(null);
    }

    private void atualizarEstadoBotoes(PedidoExame selecionado) {
        boolean temSelecao = selecionado != null;
        btnAbrirPedido.setDisable(!temSelecao);
        btnRegistrarEntrevista.setDisable(!temSelecao);
        btnRegistrarReiteracao.setDisable(!temSelecao);
        btnConcluirPedido.setDisable(!temSelecao);
    }

    private void atualizarEstadoPedidoSelecionado(PedidoExame selecionado) {
        atualizarEstadoBotoes(selecionado);
        atualizarIndicadorReiteracoes();
    }

    /**
     * Carrega os pedidos pendentes (CADASTRADO/SOLICITADO), conforme
     * já filtrado por PedidoExameDAO.listarPentendes() via o service.
     */
    private void carregarPedidos() {
        try {
            ultimaListaCompleta = pedidoExameService.listarPendentes();
            carregarIdsPedidosReiterados();
            aplicarFiltros();
        } catch (RuntimeException e) {
            ultimaListaCompleta = List.of();
            idsPedidosReiterados = new HashSet<>();
            pedidos.clear();
            atualizarIndicadorReiteracoes();
            Platform.runLater(() -> exibirErro(
                    "Não foi possível carregar os pedidos pendentes: " + mensagemErro(e)
            ));
        }
    }

    private void carregarIdsPedidosReiterados() {
        try {
            idsPedidosReiterados = reiteracaoDAO.listarTodos().stream()
                    .map(Reiteracao::getPedido)
                    .filter(pedido -> pedido != null && pedido.getId() > 0)
                    .map(PedidoExame::getId)
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            idsPedidosReiterados = new HashSet<>();
        }
    }

    private Optional<PedidoExame> pedidoSelecionado() {
        return Optional.ofNullable(tableViewPedidos.getSelectionModel().getSelectedItem());
    }

    // ===================== Handlers =====================

    @FXML
    private void handlePesquisar(ActionEvent event) {
        aplicarFiltros();
    }

    @FXML
    private void handleFiltrarReiterados() {
        filtroSomenteReiterados = !filtroSomenteReiterados;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String matricula = txtPesquisaMatricula.getText() == null
                ? ""
                : txtPesquisaMatricula.getText().trim();

        List<PedidoExame> filtrados = ultimaListaCompleta.stream()
                .filter(pedido -> matricula.isEmpty()
                        || pedido.getSentenciado() != null
                        && pedido.getSentenciado().getMatricula() != null
                        && pedido.getSentenciado().getMatricula().contains(matricula))
                .filter(pedido -> !filtroSomenteReiterados || pedidoFoiReiterado(pedido))
                .collect(Collectors.toList());

        pedidos.setAll(filtrados);
        atualizarIndicadorReiteracoes();
        tableViewPedidos.refresh();
    }

    @FXML
    private void handleNovoPedido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/NovoPedidoView.fxml"));
            Parent raiz = loader.load();

            NovoPedidoController controller = loader.getController();

            // TODO: integrar com o mecanismo real de login/sessão.
            // MainController também não recebe o usuário logado nos arquivos
            // fornecidos; assim que existir, substituir a linha abaixo por:
            // controller.setUsuarioLogado(usuarioDaSessaoAtual);
            // controller.setUsuarioLogado(usuarioLogado);

            Stage palco = new Stage();
            palco.setTitle("Novo pedido de exame");
            palco.initModality(Modality.APPLICATION_MODAL);
            palco.setScene(new Scene(raiz));
            palco.showAndWait();

            if (controller.isPedidoSalvoComSucesso()) {
                carregarPedidos();
            }
        } catch (IOException e) {
            exibirErro("Não foi possível abrir a tela de novo pedido: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleAbrirPedido(ActionEvent event) {
        abrirPedidoSelecionado();
    }

    private void abrirPedidoSelecionado() {
        Optional<PedidoExame> selecionado = pedidoSelecionado();
        if (selecionado.isEmpty()) {
            alertarSelecioneUmPedido();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/PedidoDetalheView.fxml"));
            Parent raiz = loader.load();
            PedidoDetalheController controller = loader.getController();
            controller.setPedido(selecionado.get());

            Stage palco = new Stage();
            palco.setTitle("Detalhes do pedido");
            palco.initModality(Modality.APPLICATION_MODAL);
            palco.setScene(new Scene(raiz));
            palco.showAndWait();
        } catch (IOException e) {
            exibirErro("Não foi possível abrir os detalhes do pedido: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleRegistrarEntrevista(ActionEvent event) {
        Optional<PedidoExame> selecionado = pedidoSelecionado();
        if (selecionado.isEmpty()) {
            alertarSelecioneUmPedido();
            return;
        }
        if (!validarPedidoEditavel(selecionado.get())) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/EntrevistaView.fxml"));
            Parent raiz = loader.load();
            EntrevistaController controller = loader.getController();
            controller.setPedido(selecionado.get());

            Stage palco = new Stage();
            palco.setTitle("Registrar entrevista");
            palco.initModality(Modality.APPLICATION_MODAL);
            palco.setScene(new Scene(raiz));
            palco.showAndWait();

            if (controller.isSalvoComSucesso()) {
                carregarPedidos();
            }
        } catch (IOException e) {
            exibirErro("Não foi possível abrir a tela de entrevista: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleRegistrarReiteracao(ActionEvent event) {
        Optional<PedidoExame> selecionado = pedidoSelecionado();
        if (selecionado.isEmpty()) {
            alertarSelecioneUmPedido();
            return;
        }
        if (!validarPedidoEditavel(selecionado.get())) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/ReiteracaoView.fxml"));
            Parent raiz = loader.load();
            ReiteracaoController controller = loader.getController();
            controller.setPedido(selecionado.get());

            Stage palco = new Stage();
            palco.setTitle("Registrar reiteração");
            palco.initModality(Modality.APPLICATION_MODAL);
            palco.setScene(new Scene(raiz));
            palco.showAndWait();

            if (controller.isSalvoComSucesso()) {
                carregarPedidos();
                tableViewPedidos.refresh();
            }
        } catch (IOException e) {
            exibirErro("Não foi possível abrir a tela de reiteração: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleConcluirPedido(ActionEvent event) {
        Optional<PedidoExame> selecionadoOpt = pedidoSelecionado();
        if (selecionadoOpt.isEmpty()) {
            alertarSelecioneUmPedido();
            return;
        }

        PedidoExame selecionado = selecionadoOpt.get();
        if (!validarPedidoEditavel(selecionado)) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/ConcluirPedidoView.fxml"));
            Parent raiz = loader.load();
            ConcluirPedidoController controller = loader.getController();
            controller.setPedido(selecionado);

            Stage palco = new Stage();
            palco.setTitle("Concluir pedido");
            palco.initModality(Modality.APPLICATION_MODAL);
            palco.setScene(new Scene(raiz));
            palco.showAndWait();

            if (controller.isSalvoComSucesso()) {
                carregarPedidos();
            }
        } catch (IOException e) {
            exibirErro("Não foi possível abrir a tela de conclusão: " + mensagemErro(e));
        }
    }

    @FXML
    private void handleAtualizar(ActionEvent event) {
        txtPesquisaMatricula.clear();
        carregarPedidos();
        tableViewPedidos.refresh();
    }

    // ===================== Auxiliares =====================

    /**
     * Garante que entrevistas e reiterações não sejam registradas
     * em pedidos CONCLUIDO, CANCELADO ou TRANSFERIDO.
     */
    private boolean validarPedidoEditavel(PedidoExame pedido) {
        StatusPedidoExame status = pedido.getStatus();
        boolean bloqueado = status == StatusPedidoExame.CONCLUIDO
                || status == StatusPedidoExame.CANCELADO
                || status == StatusPedidoExame.TRANSFERIDO;

        if (bloqueado) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Ação não permitida");
            alerta.setHeaderText(null);
            alerta.setContentText("Este pedido está " + status + " e não aceita novos registros.");
            alerta.showAndWait();
        }
        return !bloqueado;
    }

    private void alertarSelecioneUmPedido() {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Nenhum pedido selecionado");
        alerta.setHeaderText(null);
        alerta.setContentText("Selecione um pedido na tabela antes de continuar.");
        alerta.showAndWait();
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private boolean pedidoFoiReiterado(PedidoExame pedido) {
        return pedido != null && idsPedidosReiterados.contains(pedido.getId());
    }

    private void atualizarIndicadorReiteracoes() {
        if (lblPedidosReiterados == null) {
            return;
        }

        long totalVisivel = pedidos.stream()
                .filter(this::pedidoFoiReiterado)
                .count();

        PedidoExame selecionado = tableViewPedidos == null
                ? null
                : tableViewPedidos.getSelectionModel().getSelectedItem();

        if (selecionado != null && pedidoFoiReiterado(selecionado)) {
            lblPedidosReiterados.setText(textoIndicador("Pedido selecionado reiterado | Reiterados na lista: " + totalVisivel));
            lblPedidosReiterados.getStyleClass().remove("reiteration-indicator");
            if (!lblPedidosReiterados.getStyleClass().contains("reiteration-indicator-active")) {
                lblPedidosReiterados.getStyleClass().add("reiteration-indicator-active");
            }
            return;
        }

        lblPedidosReiterados.setText(textoIndicador("Pedidos reiterados na lista: " + totalVisivel));
        lblPedidosReiterados.getStyleClass().remove("reiteration-indicator-active");
        if (!lblPedidosReiterados.getStyleClass().contains("reiteration-indicator")) {
            lblPedidosReiterados.getStyleClass().add("reiteration-indicator");
        }
    }

    private String textoIndicador(String textoBase) {
        return filtroSomenteReiterados ? textoBase + " | filtro ativo" : textoBase;
    }

    private String mensagemErro(Throwable erro) {
        Throwable causa = erro;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null ? erro.getMessage() : causa.getMessage();
    }
}
