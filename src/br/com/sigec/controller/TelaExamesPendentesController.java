package br.com.sigec.controller;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.StatusEntrevista;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.EntrevistaService;
import br.com.sigec.service.PedidoBeneficioService;
import br.com.sigec.service.PedidoExameService;
import br.com.sigec.service.ReiteracaoService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TelaExamesPendentesController {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String STATUS_PENDENTES = "Pendentes";
    private static final String STATUS_TODOS = "Todos";
    private static final String STATUS_REITERADOS = "Reiterados";
    private static final String STATUS_PRONTOS = "Prontos para Concluir";

    @FXML
    private TextField txtMatricula;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private Button btnPesquisar;

    @FXML
    private Button btnNovo;

    @FXML
    private Button btnAtender;

    @FXML
    private Button btnReiterar;

    @FXML
    private TableView<PedidoExame> tabelaExames;

    @FXML
    private TableColumn<PedidoExame, String> colMatricula;

    @FXML
    private TableColumn<PedidoExame, String> colNome;

    @FXML
    private TableColumn<PedidoExame, LocalDate> colData;

    @FXML
    private TableColumn<PedidoExame, String> colTipoBeneficio;

    @FXML
    private TableColumn<PedidoExame, String> colPsicologo;

    @FXML
    private TableColumn<PedidoExame, String> colAssistenteSocial;

    @FXML
    private TableColumn<PedidoExame, String> colProntoParaConcluir;

    private PedidoExameService pedidoExameService;
    private EntrevistaService entrevistaService;
    private final ReiteracaoService reiteracaoService = new ReiteracaoService();
    private final PedidoBeneficioService pedidoBeneficioService = new PedidoBeneficioService();

    private final Map<Integer, String> statusPsicologoPorPedido = new HashMap<>();
    private final Map<Integer, String> statusAssistenteSocialPorPedido = new HashMap<>();
    private final Map<Integer, String> siglasBeneficioPorPedido = new HashMap<>();
    private final Set<Integer> pedidosReiterados = new HashSet<>();
    private final Set<Integer> pedidosProntosParaConcluir = new HashSet<>();

    @FXML
    public void initialize() {
        pedidoExameService = new PedidoExameService();
        entrevistaService = new EntrevistaService();

        cbStatus.setItems(FXCollections.observableArrayList(
                STATUS_PENDENTES, STATUS_TODOS, "Cadastrado", "Solicitado",
                "Concluído", "Cancelado", "Transferido", STATUS_REITERADOS, STATUS_PRONTOS
        ));
        cbStatus.setValue(STATUS_PENDENTES);

        colMatricula.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue()
                                .getSentenciado()
                                .getMatricula()
                )
        );

        colNome.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue()
                                .getSentenciado()
                                .getNome()
                )
        );

        colData.setCellValueFactory(
                cellData -> new SimpleObjectProperty<>(
                        cellData.getValue()
                                .getDataSolicitacao()
                )
        );

        colPsicologo.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        statusPsicologoPorPedido.getOrDefault(
                                cellData.getValue().getId(),
                                "-"
                        )
                )
        );

        colAssistenteSocial.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        statusAssistenteSocialPorPedido.getOrDefault(
                                cellData.getValue().getId(),
                                "-"
                        )
                )
        );

        colTipoBeneficio.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        siglasBeneficioPorPedido.getOrDefault(
                                cellData.getValue().getId(),
                                "-"
                        )
                )
        );

        colProntoParaConcluir.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        pedidosProntosParaConcluir.contains(cellData.getValue().getId())
                                ? "✔ Pronto para concluir"
                                : "-"
                )
        );

        colProntoParaConcluir.setCellFactory(coluna -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String valor, boolean vazio) {
                super.updateItem(valor, vazio);
                getStyleClass().remove("status-badge");
                getStyleClass().remove("status-realizada");
                if (vazio || valor == null || "-".equals(valor)) {
                    setText("-");
                } else {
                    setText(valor);
                    getStyleClass().addAll("status-badge", "status-realizada");
                }
            }
        });

        // Pedidos já reiterados aparecem em vermelho na lista.
        tabelaExames.setRowFactory(tabela -> new TableRow<>() {
            @Override
            protected void updateItem(PedidoExame pedido, boolean vazio) {
                super.updateItem(pedido, vazio);
                if (vazio || pedido == null) {
                    getStyleClass().remove("linha-reiterada");
                } else if (pedidosReiterados.contains(pedido.getId())) {
                    if (!getStyleClass().contains("linha-reiterada")) {
                        getStyleClass().add("linha-reiterada");
                    }
                } else {
                    getStyleClass().remove("linha-reiterada");
                }
            }
        });

        // Duplo clique numa linha abre o Resumo do pedido selecionado.
        tabelaExames.setOnMouseClicked(evento -> {
            if (evento.getButton() == MouseButton.PRIMARY && evento.getClickCount() == 2) {
                PedidoExame selecionado = tabelaExames.getSelectionModel().getSelectedItem();
                if (selecionado != null) {
                    abrirResumoPedido(selecionado);
                }
            }
        });

        pesquisar();
    }

    @FXML
    private void pesquisar() {

        List<PedidoExame> pedidos = pedidoExameService.listarTodos();
        List<Integer> todosIds = pedidos.stream().map(PedidoExame::getId).toList();

        // "Reiterados" e "Prontos para Concluir" são opções do filtro de
        // status, então esses dados precisam existir ANTES de filtrar —
        // por isso são calculados aqui para todos os pedidos, não só
        // para os que acabarem aparecendo na tabela.
        atualizarPedidosReiterados(todosIds);
        montarStatusDosAtendimentos(pedidos);

        String matricula = txtMatricula.getText() != null
                ? txtMatricula.getText().trim().toLowerCase()
                : "";
        String statusSelecionado = cbStatus.getValue();

        List<PedidoExame> filtrados = pedidos.stream()
                .filter(p -> matricula.isEmpty()
                        || p.getSentenciado().getMatricula().toLowerCase().contains(matricula))
                .filter(p -> filtraPorStatus(p, statusSelecionado))
                .toList();

        exibirPedidos(filtrados);
    }

    private void atualizarPedidosReiterados(List<Integer> ids) {
        pedidosReiterados.clear();
        reiteracaoService.listarPorPedidos(ids)
                .forEach(reiteracao -> pedidosReiterados.add(reiteracao.getPedido().getId()));
    }

    private boolean filtraPorStatus(PedidoExame pedido, String statusSelecionado) {

        if (statusSelecionado == null || STATUS_TODOS.equals(statusSelecionado)) {
            return true;
        }

        if (STATUS_REITERADOS.equals(statusSelecionado)) {
            return pedidosReiterados.contains(pedido.getId());
        }

        if (STATUS_PRONTOS.equals(statusSelecionado)) {
            return pedidosProntosParaConcluir.contains(pedido.getId());
        }

        if (STATUS_PENDENTES.equals(statusSelecionado)) {
            return pedido.getStatus() == StatusPedidoExame.CADASTRADO
                    || pedido.getStatus() == StatusPedidoExame.SOLICITADO;
        }

        return pedido.getStatus() == mapearStatus(statusSelecionado);
    }

    private StatusPedidoExame mapearStatus(String label) {
        return switch (label) {
            case "Cadastrado" -> StatusPedidoExame.CADASTRADO;
            case "Solicitado" -> StatusPedidoExame.SOLICITADO;
            case "Concluído" -> StatusPedidoExame.CONCLUIDO;
            case "Cancelado" -> StatusPedidoExame.CANCELADO;
            case "Transferido" -> StatusPedidoExame.TRANSFERIDO;
            default -> null;
        };
    }

    private void exibirPedidos(List<PedidoExame> pedidos) {

        montarSiglasBeneficios(pedidos);

        ObservableList<PedidoExame> dados = FXCollections.observableArrayList(pedidos);
        tabelaExames.setItems(dados);
    }

    private void montarSiglasBeneficios(List<PedidoExame> pedidos) {

        siglasBeneficioPorPedido.clear();

        List<Integer> ids = pedidos.stream().map(PedidoExame::getId).toList();
        List<PedidoBeneficio> pedidosBeneficio = pedidoBeneficioService.listarPorPedidosExame(ids);

        Map<Integer, List<String>> siglasPorPedido = new HashMap<>();
        for (PedidoBeneficio pedidoBeneficio : pedidosBeneficio) {
            siglasPorPedido
                    .computeIfAbsent(pedidoBeneficio.getPedido().getId(), k -> new ArrayList<>())
                    .add(pedidoBeneficio.getBeneficio().getSigla());
        }

        siglasPorPedido.forEach((idPedido, siglas) ->
                siglasBeneficioPorPedido.put(idPedido, String.join(" / ", siglas))
        );
    }

    private void montarStatusDosAtendimentos(List<PedidoExame> pedidos) {

        statusPsicologoPorPedido.clear();
        statusAssistenteSocialPorPedido.clear();
        pedidosProntosParaConcluir.clear();

        // Busca as entrevistas de todos os pedidos exibidos em uma única
        // consulta, em vez de uma consulta por linha (evita N+1).
        List<Integer> ids = pedidos.stream().map(PedidoExame::getId).toList();
        List<Entrevista> entrevistas = entrevistaService.listarPorPedidos(ids);

        Map<Integer, List<Entrevista>> entrevistasPorPedido = new HashMap<>();
        for (Entrevista entrevista : entrevistas) {
            entrevistasPorPedido
                    .computeIfAbsent(entrevista.getPedidoExame().getId(), k -> new ArrayList<>())
                    .add(entrevista);
        }

        for (PedidoExame pedido : pedidos) {

            List<Entrevista> entrevistasDoPedido =
                    entrevistasPorPedido.getOrDefault(pedido.getId(), List.of());

            Entrevista entrevistaPsicologo = buscarPorTipo(entrevistasDoPedido, TipoProfissional.PSICOLOGO);
            Entrevista entrevistaSocial = buscarPorTipo(entrevistasDoPedido, TipoProfissional.ASSISTENTE_SOCIAL);

            statusPsicologoPorPedido.put(
                    pedido.getId(),
                    formatarStatusAtendimento(entrevistaPsicologo)
            );

            statusAssistenteSocialPorPedido.put(
                    pedido.getId(),
                    formatarStatusAtendimento(entrevistaSocial)
            );

            boolean ambosRealizados =
                    entrevistaPsicologo != null && entrevistaPsicologo.getStatus() == StatusEntrevista.REALIZADA
                            && entrevistaSocial != null && entrevistaSocial.getStatus() == StatusEntrevista.REALIZADA;

            boolean aindaNaoConcluido =
                    pedido.getStatus() == StatusPedidoExame.CADASTRADO
                            || pedido.getStatus() == StatusPedidoExame.SOLICITADO;

            if (ambosRealizados && aindaNaoConcluido) {
                pedidosProntosParaConcluir.add(pedido.getId());
            }
        }
    }

    private Entrevista buscarPorTipo(List<Entrevista> entrevistas, TipoProfissional tipo) {
        for (Entrevista entrevista : entrevistas) {
            if (entrevista.getTipoAtendimento() == tipo) {
                return entrevista;
            }
        }
        return null;
    }

    private String formatarStatusAtendimento(Entrevista entrevista) {

        if (entrevista == null || entrevista.getStatus() == null) {
            return "Aguardando agendamento";
        }

        StatusEntrevista status = entrevista.getStatus();

        if (status == StatusEntrevista.REALIZADA) {
            return "Realizado";
        }

        if (status == StatusEntrevista.AGENDADA) {
            String data = entrevista.getDataAgendamento() != null
                    ? entrevista.getDataAgendamento().format(FORMATO_DATA)
                    : "";
            return "Agendado - " + data;
        }

        if (status == StatusEntrevista.CANCELADA) {
            return "Cancelado";
        }

        return "Aguardando agendamento";
    }

    @FXML
    private void atender() {
        PedidoExame selecionado = tabelaExames.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um pedido para atender.");
            return;
        }
        abrirResumoPedido(selecionado);
    }

    @FXML
    private void reiterar() {
        PedidoExame selecionado = tabelaExames.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAviso("Selecione um pedido para reiterar.");
            return;
        }

        if (!reiteracaoService.permiteReiteracao(selecionado.getStatus())) {
            mostrarAviso("Não é possível reiterar um exame " + selecionado.getStatus().name().toLowerCase() + ".");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/telaReiteracao.fxml"));
            Parent root = loader.load();

            TelaReiteracaoController controller = loader.getController();
            controller.setPedidoExame(selecionado);

            Stage stage = new Stage();
            stage.initOwner(btnReiterar.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Reiterar Pedido");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            pesquisar();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem, ButtonType.OK);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    private void novoPedido() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/telaNovoPedido.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initOwner(btnNovo.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Novo Pedido");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            pesquisar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirResumoPedido(PedidoExame pedido) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/telaResumo.fxml"));
            Parent root = loader.load();

            ResumoPedidoController controller = loader.getController();
            controller.setPedidoExame(pedido);

            Stage stage = new Stage();
            stage.initOwner(tabelaExames.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Resumo do Pedido");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            // O status do pedido/atendimentos pode ter mudado enquanto o
            // resumo estava aberto — recarrega a lista com o filtro atual.
            pesquisar();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
