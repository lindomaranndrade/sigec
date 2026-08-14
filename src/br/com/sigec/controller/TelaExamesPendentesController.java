package br.com.sigec.controller;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.service.PedidoExameService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TelaExamesPendentesController {
    @FXML
    private TextField txtMatricula;

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
    private TableColumn<PedidoExame,String> colMatricula;

    @FXML
    private TableColumn<PedidoExame,String> colNome;

    @FXML
    private TableColumn<PedidoExame, LocalDate> colData;

    @FXML
    private TableColumn<PedidoExame, String> colTipoBeneficio;

    private PedidoExameService pedidoExameService;
    @FXML
    public void initialize(){
        pedidoExameService = new PedidoExameService();
        List<PedidoExame> pedidos = pedidoExameService.listarPendentes();
        System.out.println("PEDIDOS PENDENTES: " + pedidos.size());
        ObservableList<PedidoExame> dados = FXCollections.observableArrayList(pedidos);
        tabelaExames.setItems(dados);
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


    }

    @FXML
    private void novoPedido(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/com/sigec/view/telaNovoPedido.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initOwner(btnNovo.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Novo Pedido");
            stage.setScene(new Scene(root));

            stage.setResizable(false);

            stage.showAndWait();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}
