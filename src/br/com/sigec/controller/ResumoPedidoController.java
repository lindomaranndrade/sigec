package br.com.sigec.controller;

import br.com.sigec.model.Entrevista;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Reiteracao;
import br.com.sigec.model.StatusEntrevista;
import br.com.sigec.model.StatusPedidoExame;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.EntrevistaService;
import br.com.sigec.service.PedidoExameService;
import br.com.sigec.service.ReiteracaoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ResumoPedidoController {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ---------- DADOS DO SENTENCIADO / SOLICITAÇÃO ----------

    @FXML private Label lblValorMatricula;
    @FXML private Label lblValorNome;

    @FXML private Label lblValorData;
    @FXML private Label lblValorProcesso;
    @FXML private Label lblValorBeneficios;
    @FXML private Label lblValorSituacao;
    @FXML private Label lblValorSei;

    @FXML private Label lblValorStatusSolicitacao;
    @FXML private Label lblValorDataConclusaoSolicitacao;
    @FXML private Label lblMensagemConcluido;

    // ---------- BLOCO PSICÓLOGO ----------

    @FXML private Label lblValorProfissional;
    @FXML private Label lblValorDataAgendamento;
    @FXML private Label lblValorDataRealizacao;
    @FXML private Label lblValorLaudo;
    @FXML private Label lblValorStatusAtendimento;
    @FXML private Button btnAgendarPsicologo;
    @FXML private Button btnRegistrarRealizacaoPsicologo;
    @FXML private Button btnCancelarPsicologo;

    // ---------- BLOCO ASSISTENTE SOCIAL ----------

    @FXML private Label lblValorProfissionalAssistenteSocial;
    @FXML private Label lblValorDataAgendamentoSocial;
    @FXML private Label lblValorDataRealizacaoSocial;
    @FXML private Label lblValorLaudoSocial;
    @FXML private Label lblValorStatusAtendimentoSocial;
    @FXML private Button btnAgendarSocial;
    @FXML private Button btnRegistrarRealizacaoSocial;
    @FXML private Button btnCancelarSocial;

    // ---------- REITERAÇÕES ----------

    @FXML private Label lblValorDataReiteracao;
    @FXML private Label lblValorObservacaoReiteracao;

    // ---------- RODAPÉ ----------

    @FXML private Button btnReiterar;
    @FXML private Button btnConcluir;
    @FXML private Button btnFechar;

    private final PedidoExameService pedidoExameService = new PedidoExameService();
    private final EntrevistaService entrevistaService = new EntrevistaService();
    private final ReiteracaoService reiteracaoService = new ReiteracaoService();

    private PedidoExame pedidoExame;
    private Entrevista entrevistaPsicologo;
    private Entrevista entrevistaSocial;

    /**
     * Chamado por quem abre esta tela (ex.: duplo clique na tela de Exames)
     * logo após o FXMLLoader.load(), antes do stage.show()/showAndWait().
     */
    public void setPedidoExame(PedidoExame pedidoExame) {
        this.pedidoExame = pedidoExame;
        carregarDados();
    }

    private void carregarDados() {

        // ---- Dados do sentenciado / solicitação ----

        lblValorMatricula.setText(pedidoExame.getSentenciado().getMatricula());
        lblValorNome.setText(pedidoExame.getSentenciado().getNome());

        lblValorData.setText(formatarData(pedidoExame.getDataSolicitacao()));
        lblValorProcesso.setText(pedidoExame.getNumeroProcesso());
        lblValorSei.setText(pedidoExame.getNumeroSEI() != null ? pedidoExame.getNumeroSEI() : "-");

        String status = pedidoExame.getStatus() != null ? pedidoExame.getStatus().name() : "-";
        lblValorSituacao.setText(status);
        lblValorStatusSolicitacao.setText(status);

        lblValorDataConclusaoSolicitacao.setText(formatarData(pedidoExame.getDataConclusao()));

        if (pedidoExame.getStatus() == StatusPedidoExame.CONCLUIDO) {
            lblMensagemConcluido.setText(
                    "Exame concluído em " + formatarData(pedidoExame.getDataConclusao()) + "."
            );
            lblMensagemConcluido.setVisible(true);
            lblMensagemConcluido.setManaged(true);
        } else {
            lblMensagemConcluido.setVisible(false);
            lblMensagemConcluido.setManaged(false);
        }

        boolean permiteReiteracao = reiteracaoService.permiteReiteracao(pedidoExame.getStatus());
        btnReiterar.setVisible(permiteReiteracao);
        btnReiterar.setManaged(permiteReiteracao);

        // Benefício(s): deixado com o valor já existente na tela até
        // resolvermos o carregamento de Beneficio/PedidoBeneficio.
        // lblValorBeneficios.setText(...);

        // ---- Entrevistas ----

        List<Entrevista> entrevistas = entrevistaService.listarPorPedido(pedidoExame.getId());

        // O EntrevistaDAO monta um PedidoExame "raso" (só id + numeroProcesso).
        // Substitui pelo PedidoExame completo já carregado nesta tela, para que
        // as telas de agendar/registrar e as validações do service tenham
        // sentenciado, status e dataSolicitacao disponíveis.
        for (Entrevista entrevista : entrevistas) {
            entrevista.setPedidoExame(pedidoExame);
        }

        entrevistaPsicologo = buscarPorTipo(entrevistas, TipoProfissional.PSICOLOGO);
        entrevistaSocial = buscarPorTipo(entrevistas, TipoProfissional.ASSISTENTE_SOCIAL);

        atualizarBloco(
                entrevistaPsicologo,
                lblValorProfissional,
                lblValorDataAgendamento,
                lblValorDataRealizacao,
                lblValorLaudo,
                lblValorStatusAtendimento,
                btnAgendarPsicologo,
                btnRegistrarRealizacaoPsicologo,
                btnCancelarPsicologo
        );

        atualizarBloco(
                entrevistaSocial,
                lblValorProfissionalAssistenteSocial,
                lblValorDataAgendamentoSocial,
                lblValorDataRealizacaoSocial,
                lblValorLaudoSocial,
                lblValorStatusAtendimentoSocial,
                btnAgendarSocial,
                btnRegistrarRealizacaoSocial,
                btnCancelarSocial
        );

        // ---- Reiterações ----

        List<Reiteracao> reiteracoes = reiteracaoService.listarPorPedido(pedidoExame.getId());

        if (reiteracoes.isEmpty()) {
            lblValorDataReiteracao.setText("-");
            lblValorObservacaoReiteracao.setText("Nenhuma reiteração registrada.");
        } else {
            Reiteracao ultima = reiteracoes.get(0);
            lblValorDataReiteracao.setText(formatarData(ultima.getDataReiteracao()));
            lblValorObservacaoReiteracao.setText(
                    ultima.getObservacoes() != null ? ultima.getObservacoes() : "-"
            );
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

    private void atualizarBloco(
            Entrevista entrevista,
            Label lblProfissional,
            Label lblDataAgendamento,
            Label lblDataRealizacao,
            Label lblLaudo,
            Label lblStatus,
            Button btnAgendar,
            Button btnRegistrarRealizacao,
            Button btnCancelar
    ) {

        if (entrevista == null) {
            lblProfissional.setText("Não definido");
            lblDataAgendamento.setText("-");
            lblDataRealizacao.setText("-");
            lblLaudo.setText("-");
            lblStatus.setText("-");
            esconder(btnAgendar, btnRegistrarRealizacao, btnCancelar);
            return;
        }

        lblProfissional.setText(
                entrevista.getProfissional() != null
                        ? entrevista.getProfissional().getNome()
                        : "Não definido"
        );

        lblDataAgendamento.setText(formatarData(entrevista.getDataAgendamento()));
        lblDataRealizacao.setText(formatarData(entrevista.getDataRealizacao()));

        lblLaudo.setText(
                entrevista.getDataEntregaLaudo() != null ? "Entregue" : "Pendente"
        );

        StatusEntrevista status = entrevista.getStatus();
        lblStatus.setText(formatarStatus(status));

        atualizarClasseStatus(lblStatus, status);

        switch (status) {
            case PENDENTE_AGENDAMENTO -> {
                mostrar(btnAgendar);
                esconder(btnRegistrarRealizacao, btnCancelar);
            }
            case AGENDADA -> {
                mostrar(btnRegistrarRealizacao, btnCancelar);
                esconder(btnAgendar);
            }
            default -> esconder(btnAgendar, btnRegistrarRealizacao, btnCancelar); // REALIZADA / CANCELADA
        }
    }

    private String formatarStatus(StatusEntrevista status) {
        return switch (status) {
            case PENDENTE_AGENDAMENTO -> "Aguardando agendamento";
            case AGENDADA -> "Agendado";
            case REALIZADA -> "Realizado";
            case CANCELADA -> "Cancelado";
        };
    }

    private void atualizarClasseStatus(Label lbl, StatusEntrevista status) {
        lbl.getStyleClass().removeAll(
                "status-pendente", "status-agendada", "status-realizada", "status-cancelada"
        );
        if (!lbl.getStyleClass().contains("status-badge")) {
            lbl.getStyleClass().add("status-badge");
        }
        switch (status) {
            case PENDENTE_AGENDAMENTO -> lbl.getStyleClass().add("status-pendente");
            case AGENDADA -> lbl.getStyleClass().add("status-agendada");
            case REALIZADA -> lbl.getStyleClass().add("status-realizada");
            case CANCELADA -> lbl.getStyleClass().add("status-cancelada");
        }
    }

    private void mostrar(Button... botoes) {
        for (Button botao : botoes) {
            botao.setVisible(true);
            botao.setManaged(true);
        }
    }

    private void esconder(Button... botoes) {
        for (Button botao : botoes) {
            botao.setVisible(false);
            botao.setManaged(false);
        }
    }

    private String formatarData(java.time.LocalDate data) {
        return data != null ? data.format(FORMATO_DATA) : "-";
    }

    // ============================================================
    // AÇÕES - PSICÓLOGO
    // ============================================================

    @FXML
    private void agendarPsicologo() {
        abrirTelaAgendar(entrevistaPsicologo);
    }

    @FXML
    private void registrarRealizacaoPsicologo() {
        abrirTelaRegistrarRealizacao(entrevistaPsicologo);
    }

    @FXML
    private void cancelarPsicologo() {
        cancelarComConfirmacao(entrevistaPsicologo);
    }

    // ============================================================
    // AÇÕES - ASSISTENTE SOCIAL
    // ============================================================

    @FXML
    private void agendarSocial() {
        abrirTelaAgendar(entrevistaSocial);
    }

    @FXML
    private void registrarRealizacaoSocial() {
        abrirTelaRegistrarRealizacao(entrevistaSocial);
    }

    @FXML
    private void cancelarSocial() {
        cancelarComConfirmacao(entrevistaSocial);
    }

    // ============================================================
    // AÇÕES COMUNS
    // ============================================================

    private void abrirTelaAgendar(Entrevista entrevista) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/com/sigec/view/telaAgendarAtendimento.fxml")
            );
            Parent root = loader.load();

            TelaAgendarAtendimentoController controller = loader.getController();
            controller.setEntrevista(entrevista);

            Stage stage = new Stage();
            stage.initOwner(btnFechar.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Agendar Atendimento");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarDados();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaRegistrarRealizacao(Entrevista entrevista) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/com/sigec/view/telaRegistrarRealizacao.fxml")
            );
            Parent root = loader.load();

            TelaRegistrarRealizacaoController controller = loader.getController();
            controller.setEntrevista(entrevista);

            Stage stage = new Stage();
            stage.initOwner(btnFechar.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Registrar Realização");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarDados();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelarComConfirmacao(Entrevista entrevista) {

        if (entrevista == null) {
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja cancelar este agendamento?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacao.setTitle("Cancelar Agendamento");
        confirmacao.setHeaderText(null);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            try {
                entrevistaService.cancelarAgendamento(entrevista);
                carregarDados();
            } catch (IllegalArgumentException e) {
                Alert erro = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                erro.setTitle("Não foi possível cancelar");
                erro.setHeaderText(null);
                erro.showAndWait();
            }
        }
    }

    // ============================================================
    // RODAPÉ
    // ============================================================

    @FXML
    private void reiterar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/com/sigec/view/telaReiteracao.fxml")
            );
            Parent root = loader.load();

            TelaReiteracaoController controller = loader.getController();
            controller.setPedidoExame(pedidoExame);

            Stage stage = new Stage();
            stage.initOwner(btnFechar.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Reiterar Pedido");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            if (controller.isSalvo()) {
                carregarDados();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void concluir() {

        if (pedidoExame.getNumeroSEI() == null || pedidoExame.getNumeroSEI().trim().isEmpty()) {

            TextInputDialog dialogoSei = new TextInputDialog();
            dialogoSei.setTitle("Número SEI");
            dialogoSei.setHeaderText(null);
            dialogoSei.setContentText("Informe o número SEI para concluir o pedido:");

            Optional<String> numeroSei = dialogoSei.showAndWait();

            if (numeroSei.isEmpty() || numeroSei.get().trim().isEmpty()) {
                return;
            }

            pedidoExame.setNumeroSEI(numeroSei.get().trim());
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja concluir este pedido?",
                ButtonType.YES, ButtonType.NO
        );
        confirmacao.setTitle("Concluir Pedido");
        confirmacao.setHeaderText(null);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            try {
                pedidoExameService.concluirPedido(pedidoExame);
                carregarDados();
            } catch (IllegalArgumentException e) {
                Alert erro = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                erro.setTitle("Não foi possível concluir");
                erro.setHeaderText(null);
                erro.showAndWait();
            }
        }
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}
