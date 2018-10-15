package Ejercicios;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;
import static simlib.SimLib.*;

import java.io.IOException;

public class FilaServidor{

    private static final byte ARRIBO = 1, EVALUAR_PERMANENCIA = 2, SALIDA = 3, FIN_SIMULACION = 4;

    private static int STREAM_LLEGADAS, STREAM_SERVICIO, STREAM_TOLERANCIA, STREAM_ESPERA, STREAM_PERMANENCIA;

    private static float media_llegada, media_servicio, tolerancia_A, tolerancia_B, tolerancia_C,
            media_espera, permanencia_A, permanencia_B;

    private static SimReader reader;
    private static SimWriter writer;

    private static Resource<Float> servidor;
    private static Queue<Float> clientes;
    private static DiscreteStat demoras, demoraAbandonos;

    public static void main(String[] args) throws Exception{

        reader = new SimReader("FilaServidor.in");
        writer = new SimWriter("FilaServidor.out");

        media_llegada = reader.readFloat();
        media_servicio = reader.readFloat();
        tolerancia_A = reader.readFloat();
        tolerancia_B = reader.readFloat();
        tolerancia_C = reader.readFloat();
        media_espera = reader.readFloat();
        permanencia_A = reader.readFloat();
        permanencia_B = reader.readFloat();

        int tiempo_simulacion = reader.readInt();
        int cantidad_ejecuciones = reader.readInt();

        //TODO: encabezado reporte

        for(int i = 0; i < cantidad_ejecuciones; i++){

            //Cambio en los Streams para cada una de las ejecuciones
            STREAM_LLEGADAS     = i + 1;
            STREAM_SERVICIO     = i + 2;
            STREAM_TOLERANCIA   = i + 3;
            STREAM_ESPERA       = i + 4;
            STREAM_PERMANENCIA  = i + 5;


            writer.write("Simulacion #" + (i + 1) + "\n\n");

            initSimlib();

            servidor = new Resource<>("Servidor");
            clientes = new Queue<>("Clientes");
            demoras = new DiscreteStat("Demora Clientes");
            demoraAbandonos = new DiscreteStat("Demora Clientes que Abandonan");

            eventSchedule(expon(media_llegada, STREAM_LLEGADAS), ARRIBO);
            eventSchedule(tiempo_simulacion, FIN_SIMULACION);

            do {
                timing();


                switch (eventType){
                    case ARRIBO:
                        arribo();
                        break;

                    case EVALUAR_PERMANENCIA:
                        evaluar();
                        break;

                    case SALIDA:
                        salida();
                        break;

                    case FIN_SIMULACION:
                        reporte();
                        break;
                }

            }while(eventType != FIN_SIMULACION);
        }

        reader.close();
        writer.close();

    }


    private static void arribo(){

        eventSchedule(simTime + expon(media_llegada, STREAM_LLEGADAS), ARRIBO);

        if(servidor.isIdle()){

            demoras.record(0f);
            eventSchedule(simTime + expon(media_servicio, STREAM_SERVICIO), SALIDA);
            servidor.emplace(simTime);

        }else{

            if(Math.ceil(triag(tolerancia_B, tolerancia_A, tolerancia_C, STREAM_TOLERANCIA)) >= clientes.size()){
                clientes.offer(simTime);
                eventSchedule(simTime + erlang(2, media_espera, STREAM_ESPERA), EVALUAR_PERMANENCIA, simTime);
            }else{
                demoraAbandonos.record(0f);
            }

        }
    }

    private static void evaluar(){
        float cliente = eventAttributes[0];
        int posicion = clientes.search(cliente);
        if( posicion != -1){
            if(posicion > unifrm(permanencia_A, permanencia_B, STREAM_PERMANENCIA)){
                demoraAbandonos.record(simTime - cliente);
                clientes.remove(cliente);
            }
        }
    }

    private static void salida(){
        demoras.record(simTime - servidor.getElement());

        if(!clientes.isEmpty()){
            servidor.replace(clientes.poll());
            eventSchedule(simTime + expon(media_servicio, STREAM_SERVICIO), SALIDA);
        }else{
            servidor.remove();
        }
    }

    private static void reporte() throws IOException{
        servidor.report(writer);
        demoras.report(writer);
        demoraAbandonos.report(writer);
    }

}
