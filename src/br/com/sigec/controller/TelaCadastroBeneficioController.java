package br.com.sigec.controller;

import br.com.sigec.model.Beneficio;
import br.com.sigec.service.BeneficioService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TelaCadastroBeneficioController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtDescricao;
    @FXML private TextField txtSigla;
    @FXML private Label lblMensagemErro;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    private final BeneficioService beneficioService = new BeneficioService();

    private Beneficio beneficio;
    private boolean salvo = false;

    public void setBeneficio(Beneficio beneficio) {
        this.beneficio = beneficio;

        lblTitulo.setText("EDITAR BENEFÍCIO");
        txtDescricao.setText(beneficio.getDescricao());
        txtSigla.setText(beneficio.getSigla());
    }

    public boolean isSalvo() {
        return salvo;
    }

    @FXML
    private void salvar() {

        esconderErro();

        String descricao = txtDescricao.getText();
        String sigla = txtSigla.getText();

        try {
            if (beneficio == null) {
                beneficioService.inserir(new Beneficio(descricao, sigla));
            } else {
                beneficio.setDescricao(descricao);
                beneficio.setSigla(sigla);
                beneficioService.atualizar(beneficio);
            }

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
