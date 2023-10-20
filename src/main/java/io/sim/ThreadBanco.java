package io.sim;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

import java.sql.Timestamp;

public class ThreadBanco extends Thread{

    private Socket socket;
    private Conta pagador;
    private Conta recebedor;
    private double valor;
    private Banco banco;
    private boolean on_ff;
    
    public ThreadBanco (Socket s, Banco banco) { 
        this.socket = s;
        this.banco = banco;
        this.on_ff = true; 
    }

    @Override
    public void run() {

        try {
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            while(on_ff){
                
                int tam = entrada.readInt();  
                byte[] cripto = entrada.readNBytes(tam);
                String mensagem = Criptografia.decrypt(cripto);
                JSONObject obj = JSON.dadosJSON2Transacao(mensagem);
                try{
                if(obj.getString("acabou").equals("true")){
                    on_ff = false;
                    break;
                }
                }catch(Exception e){

                }

                pagador = banco.getConta(obj.getInt("pagador"));
                recebedor = banco.getConta(obj.getInt("recebedor"));
                valor =  obj.getDouble("valor");

                String login = obj.getString("login");
                String senha = obj.getString("senha");

                if(this.banco.confereConta(login,senha)){
                    pagamento();
                } else{
                    System.out.println("senha incorreta");
                }
                
                
        }


        System.out.println("Thread banco off");
           
        } catch (IOException ioe) {
            System.out.println("Erro:2 " + ioe.toString());
        }

        catch (Exception e) {

            e.printStackTrace();
        }
        
        
    }


    public boolean transacao(){

		if(pagador.getSaldo() < valor){
			return false;
			
		}
		else{
			pagador.setSaldo(pagador.getSaldo() - valor);
			recebedor.setSaldo(recebedor.getSaldo() + valor);
			return true;
		}
	}

    public void pagamento(){
        if(transacao()){
            System.out.println("Saldo conta " + recebedor.getId() + " " + recebedor.getSaldo());
            System.out.println("Saldo conta " + pagador.getId() + " " + pagador.getSaldo());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Transacao transacao = new Transacao(pagador.getId(), recebedor.getId(), valor, timestamp);
            
            Banco.addTransacao(transacao);
        }
        else{
            System.out.println("Saldo insuficienete");
        }
    }

    public void setOn_off(boolean on_ff){
        this.on_ff = on_ff;
    }
}
