package br.com.sigec.controller;

import br.com.sigec.dao.SentenciadoDAO;
import br.com.sigec.model.Beneficio;
import br.com.sigec.model.PedidoBeneficio;
import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.service.BeneficioService;
import br.com.sigec.service.PedidoExameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TelaNovoPedidoController {
    @FXML
    private TextField txtSentenciado;

    @FXML
    private Button btnPesquisar;

    @FXML
    private TextField txtNumeroProcesso;

    @FXML
    private ComboBox<Beneficio> cbBeneficio;

    @FXML
    private Button btnAdicionar;

    @FXML
    private ListView<Beneficio> listaBeneficios;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSalvar;

    @FXML
    private Label lblSentenciado;

    @FXML
    private DatePicker dpDataSolicitacao;

    @FXML
    public void initialize(){
        BeneficioService beneficioService = new BeneficioService();
        cbBeneficio.getItems().addAll(beneficioService.listarTodos());

        //faz o enter funcionar para a pesquisa do sentenciado
        txtSentenciado.setOnAction(event -> pesquisarSentenciado());


    }


    @FXML
    private void cancelar(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private Sentenciado sentenciadoSelecionado;
    @FXML
    private void salvar() {
        String processo = txtNumeroProcesso.getText();
        Sentenciado sentenciado = sentenciadoSelecionado;
        LocalDate dataSolicitacao = dpDataSolicitacao.getValue();

        ArrayList<PedidoBeneficio> pedidoBeneficios = new ArrayList<>();

        System.out.println("CLIQUEI EM SALVAR");

        try {

            PedidoExame pedidoExame =
                    new PedidoExame(sentenciado, dataSolicitacao, processo);

            System.out.println("PEDIDO CRIADO");

            for (Beneficio beneficio : listaBeneficios.getItems()) {

                System.out.println(
                        "VERIFICANDO BENEFÍCIO: " + beneficio.getDescricao()
                );

                PedidoBeneficio pedidoBeneficio =
                        new PedidoBeneficio(pedidoExame, beneficio);

                pedidoBeneficios.add(pedidoBeneficio);
            }

            System.out.println(
                    "BENEFÍCIOS MONTADOS: " + pedidoBeneficios.size()
            );

            pedidoExame.setPedidosBeneficios(pedidoBeneficios);

            System.out.println("LISTA COLOCADA NO PEDIDO");

            PedidoExameService pedidoExameService =
                    new PedidoExameService();

            System.out.println("CHAMANDO SERVICE");

            pedidoExameService.inserir(pedidoExame);

            System.out.println("PEDIDO INSERIDO");

            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();

        } catch (IllegalArgumentException e) {

            System.out.println("ERRO DO SERVICE: " + e.getMessage());

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText("Não foi possível salvar o pedido");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void pesquisarSentenciado(){
        String matricula = txtSentenciado.getText();

        if(matricula.isBlank()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Matrícula obrigatória");
            alert.setHeaderText("Matrícula não informada!");
            alert.setContentText("Digite a matrícula do sentenciado para realizar a pesquisa.");
            alert.showAndWait();
            return;
        }

        SentenciadoDAO sentenciadoDAO = new SentenciadoDAO();
        sentenciadoSelecionado = sentenciadoDAO.buscarPorMatricula(matricula);

        if(sentenciadoSelecionado == null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Sentenciado não encontrado!");
            alert.setHeaderText("Matrícula não localizada");
            alert.setContentText("A matrícula " + matricula + " não foi encontrada.\n\nDeseja cadastrar o sentenciado?");
            ButtonType sim = new ButtonType("Sim");
            ButtonType nao = new ButtonType("Não");
            alert.getButtonTypes().setAll(sim, nao);

            ButtonType resposta = alert.showAndWait().orElse(nao);

            if(resposta == sim){
                System.out.println("CLICOU EM SIM");
                try{
                    System.out.println("TENTANDO CARREGAR FXML");
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/br/com/sigec/view/telaCadastroSentenciado.fxml")
                    );

                    System.out.println("FXML CARREGADO");
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    stage.setTitle("Cadastro de Sentenciado");
                    stage.setScene(new Scene(root));
                    System.out.println("ABRINDO JANELA");
                    stage.showAndWait();

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return;
        }

        lblSentenciado.getStyleClass().remove("sentenciado-info");
        lblSentenciado.getStyleClass().add("sentenciado-info-found");
        lblSentenciado.setText("Sentenciado: " + sentenciadoSelecionado.getNome());

        System.out.println(
                "Sentenciado encontrado: "
                        + sentenciadoSelecionado.getNome()
        );
    }

    @FXML
    private void adicionarBeneficio(){
        Beneficio beneficio = cbBeneficio.getValue();
        if (beneficio == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Benefício");
            alert.setHeaderText("Nenhum benefício selecionado");
            alert.setContentText("Selecione um benefício antes de adicionar.");
            alert.showAndWait();
            return;
        }

        if(listaBeneficios.getItems().contains(beneficio)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Benefício");
            alert.setHeaderText("Benefício já selecionado");
            alert.setContentText("O benefício não pode ser duplicado");
            alert.showAndWait();
            return;
        }
        listaBeneficios.getItems().add(beneficio);

    }

    @FXML
    private Button btnRemover;

    @FXML
    private void removerBeneficio() {

        Beneficio beneficioSelecionado =
                listaBeneficios.getSelectionModel().getSelectedItem();

        if (beneficioSelecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Benefício");
            alert.setHeaderText("Nenhum benefício selecionado");
            alert.setContentText("Selecione um benefício na lista para remover.");
            alert.showAndWait();
            return;
        }

        listaBeneficios.getItems().remove(beneficioSelecionado);
    }
}
