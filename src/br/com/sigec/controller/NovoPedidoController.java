package br.com.sigec.controller;

import br.com.sigec.dao.BeneficioDAO;
import br.com.sigec.dao.SentenciadoDAO;
import br.com.sigec.model.Beneficio;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.Usuario;
import br.com.sigec.service.PedidoBeneficioService;
import br.com.sigec.service.PedidoExameService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller da tela "Novo pedido de exame".
 *
 * Métodos reais utilizados:
 *   - BeneficioDAO.listarTodos()
 *   - SentenciadoDAO.buscarPorMatricula(String)         (não há método equivalente
 *     em SentenciadoService, então a busca é feita direto no DAO)
 *   - PedidoExameService.inserir(PedidoExame)
 *   - PedidoBeneficioService.inserir(PedidoBeneficio)
 *
 * Ponto que depende de integração futura:
 *   1. Não existe mecanismo de sessão/usuário logado nos arquivos fornecidos —
 *      o Usuario usado no pedido precisa ser injetado por quem abrir esta tela.
 */
public class NovoPedidoController implements Initializable {

    @FXML private TextField txtMatricula;
    @FXML private TextField txtNomeSentenciado;
    @FXML private TextField txtNumeroProcesso;
    @FXML private DatePicker dpDataSolicitacao;
    @FXML private ListView<Beneficio> listViewBeneficios;
    @FXML private Button btnPesquisarSentenciado;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    // Usado direto pois SentenciadoService não expõe busca por matrícula
    private final BeneficioDAO beneficioDAO = new BeneficioDAO();
    private final SentenciadoDAO sentenciadoDAO = new SentenciadoDAO();
    private final PedidoExameService pedidoExameService = new PedidoExameService();
    private final PedidoBeneficioService pedidoBeneficioService = new PedidoBeneficioService();

    private Sentenciado sentenciadoEncontrado;

    // TODO: integrar com o mecanismo real de login/sessão.
    // Nenhum dos arquivos enviados (Usuario, UsuarioService) expõe usuário logado,
    // então quem abrir esta tela precisa chamar setUsuarioLogado(...) antes de exibir.
    private Usuario usuarioLogado;

    private boolean pedidoSalvoComSucesso = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarListaBeneficios();
        carregarBeneficios();

        btnSalvar.setDisable(true);
    }

    private void configurarListaBeneficios() {
        listViewBeneficios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewBeneficios.setPlaceholder(new Label("Nenhum benefício cadastrado."));
        listViewBeneficios.setCellFactory(lista -> new ListCell<Beneficio>() {
            @Override
            protected void updateItem(Beneficio beneficio, boolean empty) {
                super.updateItem(beneficio, empty);

                if (empty || beneficio == null) {
                    setText(null);
                    return;
                }

                String sigla = beneficio.getSigla() == null ? "" : beneficio.getSigla().trim();
                String descricao = beneficio.getDescricao() == null ? "" : beneficio.getDescricao().trim();
                setText(sigla.isEmpty() ? descricao : sigla + " - " + descricao);
            }
        });
    }

    private void carregarBeneficios() {
        try {
            listViewBeneficios.setItems(FXCollections.observableArrayList(beneficioDAO.listarTodos()));
        } catch (RuntimeException e) {
            listViewBeneficios.setItems(FXCollections.observableArrayList());
            exibirErro("Não foi possível carregar os benefícios cadastrados: " + mensagemErro(e));
        }
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public boolean isPedidoSalvoComSucesso() {
        return pedidoSalvoComSucesso;
    }

    // ===================== Handlers =====================

    @FXML
    private void handlePesquisarSentenciado(ActionEvent event) {
        String matricula = txtMatricula.getText() == null ? "" : txtMatricula.getText().trim();

        if (matricula.isEmpty()) {
            exibirAviso("Informe a matrícula para pesquisar.");
            return;
        }

        Sentenciado encontrado;
        try {
            encontrado = sentenciadoDAO.buscarPorMatricula(matricula);
        } catch (RuntimeException e) {
            exibirErro("Não foi possível pesquisar o sentenciado: " + mensagemErro(e));
            return;
        }

        if (encontrado == null) {
            sentenciadoEncontrado = null;
            txtNomeSentenciado.clear();
            btnSalvar.setDisable(true);
            confirmarCadastroSentenciado(matricula);
            return;
        }

        sentenciadoEncontrado = encontrado;
        txtNomeSentenciado.setText(encontrado.getNome());
        btnSalvar.setDisable(false);
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        if (sentenciadoEncontrado == null) {
            exibirAviso("Pesquise e selecione um sentenciado antes de salvar.");
            return;
        }

        String numeroProcesso = txtNumeroProcesso.getText() == null ? "" : txtNumeroProcesso.getText().trim();
        LocalDate dataSolicitacao = dpDataSolicitacao.getValue();

        Usuario usuarioResponsavel = usuarioLogado == null ? usuarioTemporario() : usuarioLogado;

        PedidoExame pedidoExame = new PedidoExame(
                sentenciadoEncontrado,
                dataSolicitacao,
                numeroProcesso,
                usuarioResponsavel
        );

        try {
            // Validações (sentenciado/usuario obrigatórios, data não futura etc.)
            // já acontecem dentro do service.
            pedidoExameService.inserir(pedidoExame);
        } catch (RuntimeException e) {
            exibirErro(e.getMessage());
            return;
        }

        vincularBeneficiosSelecionados(pedidoExame);

        pedidoSalvoComSucesso = true;
        fecharJanela();
    }

    private void vincularBeneficiosSelecionados(PedidoExame pedidoExame) {
        List<Beneficio> selecionados = listViewBeneficios.getSelectionModel().getSelectedItems();

        StringBuilder falhas = new StringBuilder();

        for (Beneficio beneficio : selecionados) {
            PedidoBeneficio vinculo = new PedidoBeneficio(pedidoExame, beneficio);
            try {
                pedidoBeneficioService.inserir(vinculo);
            } catch (RuntimeException e) {
                falhas.append("- ").append(beneficio.getDescricao())
                        .append(": ").append(mensagemErro(e)).append("\n");
            }
        }

        if (falhas.length() > 0) {
            exibirAviso("Pedido salvo, mas alguns benefícios não foram vinculados:\n" + falhas);
        }
    }

    private void abrirCadastroSentenciado(String matricula) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/CadastroSentenciadoView.fxml"));
            Parent raiz = loader.load();

            CadastroSentenciadoController controller = loader.getController();
            controller.setMatriculaInicial(matricula);

            Stage palco = new Stage();
            palco.setTitle("Cadastrar sentenciado");
            palco.initModality(Modality.APPLICATION_MODAL);
            palco.setScene(new Scene(raiz));
            palco.showAndWait();

            if (controller.isSalvoComSucesso()) {
                definirSentenciadoEncontrado(controller.getSentenciadoCadastrado());
            }
        } catch (IOException e) {
            exibirErro("Não foi possível abrir o cadastro de sentenciado: " + mensagemErro(e));
        }
    }

    private void confirmarCadastroSentenciado(String matricula) {
        Alert alerta = new Alert(
                AlertType.CONFIRMATION,
                "Matrícula não encontrada. Deseja cadastrar este sentenciado?",
                ButtonType.YES,
                ButtonType.NO
        );
        alerta.setTitle("Sentenciado não encontrado");
        alerta.setHeaderText(null);

        Optional<ButtonType> resposta = alerta.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            abrirCadastroSentenciado(matricula);
        }
    }

    private void definirSentenciadoEncontrado(Sentenciado sentenciado) {
        sentenciadoEncontrado = sentenciado;
        txtMatricula.setText(sentenciado.getMatricula());
        txtNomeSentenciado.setText(sentenciado.getNome());
        btnSalvar.setDisable(false);
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        pedidoSalvoComSucesso = false;
        fecharJanela();
    }

    // ===================== Auxiliares =====================

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void exibirAviso(String mensagem) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void exibirErro(String mensagem) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private String mensagemErro(Throwable erro) {
        Throwable causa = erro;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null ? erro.getMessage() : causa.getMessage();
    }

    private Usuario usuarioTemporario() {
        Usuario usuario = new Usuario(1, "temporario");
        usuario.setAtivo(true);
        return usuario;
    }
}
