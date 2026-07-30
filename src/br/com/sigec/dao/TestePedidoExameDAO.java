package br.com.sigec.dao;

import br.com.sigec.model.PedidoExame;
import br.com.sigec.model.Sentenciado;
import br.com.sigec.model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestePedidoExameDAO {
    public static void main(String[] args) {

        PedidoExameDAO pedidoDAO = new PedidoExameDAO();

        PedidoExame pedido = pedidoDAO.buscarPorId(3);



        List<PedidoExame> pedidos = new ArrayList<>();
        pedidos = pedidoDAO.listarTodos();
        for(PedidoExame p: pedidos){
            System.out.println("p = " + p);
        }
    }
}
