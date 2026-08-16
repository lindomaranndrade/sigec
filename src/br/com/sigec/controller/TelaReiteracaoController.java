package br.com.sigec.controller;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Reiteracao;
import br.com.sigec.service.ReiteracaoService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TelaReiteracaoController {

    @FXML private Label lblResumoSentenciado;
    @FXML private DatePicker dpDataReiteracao;
    @FXML private TextField txtDespacho;
    @FXML private TextArea txtObservacoes;
    @FXML private Label lblMensagemErro;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    private final ReiteracaoService reiteracaoService = new ReiteracaoService();

    private PedidoExame pedidoExame;
    private boolean salvo = false;

    public void setPedidoExame(PedidoExame pedidoExame) {
        this.pedidoExame = pedidoExame;
        lblResumoSentenciado.setText(
                "Sentenciado: " + pedidoExame.getSentenciado().getNome()
        );
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    private void salvar() {

        esconderErro();

        try {
            Reiteracao reiteracao = new Reiteracao(
                    pedidoExame,
                    dpDataReiteracao.getValue(),
                    txtObservacoes.getText(),
                    txtDespacho.getText()
            );
            reiteracaoService.inserir(reiteracao);

            salvo = true;
            fechar();
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void mostrarErro(String mensagem) {
        lblMensagemErro.setText(mensagem);
        lblMensagemErro.setVisible(true);
        lblMensagemErro.setManaged(true);
    }

    private void esconderErro() {
        lblMensagemErro.setVisible(false);
        lblMensagemErro.setManaged(false);
    }

    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
