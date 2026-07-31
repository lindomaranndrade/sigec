package br.com.sigec.service;

import br.com.sigec.model.Beneficio;

public class BeneficioServiceTeste {
    public static void main(String[] args) {
        BeneficioService service = new BeneficioService();

        testarBeneficioNulo(service);
        testarDescricaoNula(service);
        testarDescricaoVazia(service);
        testarDescricaoDuplicada(service);
    }

    private static void testarBeneficioNulo(BeneficioService service) {
        try {
            service.inserir(null);
        } catch (Exception e) {
            System.out.println("Teste Benefício Nulo:");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarDescricaoNula(BeneficioService service) {
        try {
            Beneficio beneficio = new Beneficio();
            beneficio.setDescricao(null);

            service.inserir(beneficio);

        } catch (Exception e) {
            System.out.println("Teste Descrição Nula:");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarDescricaoVazia(BeneficioService service) {
        try {
            Beneficio beneficio = new Beneficio();
            beneficio.setDescricao("");

            service.inserir(beneficio);

        } catch (Exception e) {
            System.out.println("Teste Descrição Vazia:");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }

    private static void testarDescricaoDuplicada(BeneficioService service) {
        try {
            Beneficio beneficio = new Beneficio();

            // coloque aqui uma descrição que já existe no banco
            beneficio.setDescricao("Progressão de Regime");

            service.inserir(beneficio);

        } catch (Exception e) {
            System.out.println("Teste Descrição Duplicada:");
            System.out.println(e.getMessage());
            System.out.println();
        }
    }
    }

