package br.com.ambev.orderprocessing.core.exception;

public class ConsumerException extends RuntimeException{
    public ConsumerException(String mensagem){
        super("Erro ao consumir mensagem! Causa: "+mensagem);
    }
}
