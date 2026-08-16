package br.com.sigec.controller;

import br.com.sigec.model.Profissional;
import br.com.sigec.model.TipoProfissional;
import br.com.sigec.service.ProfissionalService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class TelaCadastroProfissionalController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNome;
    @FXML private ComboBox<TipoProfissional> cbTipo;
    @FXML private CheckBox chkAtivo;
    @FXML private Label lblMensagemErro;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    private final ProfissionalService profissionalService = new ProfissionalService();

    private Profissional profissional;

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList(TipoProfissional.values()));
        cbTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoProfissional tipo) {
                return formatarTipo(tipo);
            }

            @Override
            public TipoProfissional fromString(String string) {
                return null;
            }
        });
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;

        lblTitulo.setText("EDITAR PROFISSIONAL");
        txtNome.setText(profissional.getNome());
        cbTipo.setValue(profissional.getTipo());
        chkAtivo.setSelected(profissional.isAtivo());
    }

    private String formatarTipo(TipoProfissional tipo) {
        if (tipo == null) {
            return "";
        }
        return switch (tipo) {
            case PSICOLOGO -> "Psicólogo";
            case ASSISTENTE_SOCIAL -> "Assistente Social";
        };
    }

    @FXML
    private void salvar() {

        esconderErro();

        String nome = txtNome.getText();
        TipoProfissional tipo = cbTipo.getValue();
        boolean ativo = chkAtivo.isSelected();

        try {
            if (profissional == null) {
                Profissional novo = new Profissional(nome, tipo);
                novo.setAtivo(ativo);
                profissionalService.inserir(novo);
            } else {
                profissional.setNome(nome);
                profissional.setTipo(tipo);
                profissional.setAtivo(ativo);
                profissionalService.atualizar(profissional);
            }
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
