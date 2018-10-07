package Ejercicios;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;
import static simlib.SimLib.*;

import java.io.IOException;

public class CadenaHotelera {

    static final byte EVENTO_LLEGADA_BUS1_HOTEL1 = 1, EVENTO_LLEGADA_BUS1_HOTEL2 = 2, EVENTO_LLEGADA_BUS1_AEREOPUERTO = 3,
            EVENTO_LLEGADA_BUS2_HOTEL1 = 4, EVENTO_LLEGADA_BUS2_HOTEL2 = 5, EVENTO_LLEGADA_BUS2_AEREOPUERTO = 6,
            EVENTO_LLEGADA_PASAJERO_HOTEL1 = 7, EVENTO_LLEGADA_PASAJERO_HOTEL2 = 8, EVENTO_LLEGADA_PASAJERO_AEREOPUERTO = 9, EVENTO_FINALIZAR_SIMULACION = 10;

    static final byte STREAM_BUS_1 = 1, STREAM_BUS_2 = 2, STREAM_LEGADA_VUELO = 3, STREAM_LEGADA_HOTEL_1 = 4, STREAM_LEGADA_HOTEL_2 = 5;

    static int finDeLaSimulacion, aeroBus1MidLess, aeroBus1MidMax, aeroBus2MidLess, aeroBus2MidMax;
    static float mediaBus,desviacionBus,mediaDeLlegadaPasajerosVuelo,mediaDeLlegadaPasajerosHotel,salidaSegundoBus;

    static SimReader reader;
    static SimWriter writer;

    static Queue<Float> colaAereopuerto;
    static Queue<Float> colaHotel1;
    static Queue<Float> colaHotel2;

    static ContinStat Bus1;
    static ContinStat Bus2;

    public static void main(String[] args) throws Exception {
        /* Open inputs and outputs files */
        reader = new SimReader("CadenaHotelera.in");
        writer = new SimWriter("CadenaHotelera.out");

        finDeLaSimulacion = reader.readInt();
        mediaBus = reader.readFloat();
        desviacionBus = reader.readFloat();
        mediaDeLlegadaPasajerosVuelo = reader.readFloat();
        mediaDeLlegadaPasajerosHotel = reader.readFloat();
        salidaSegundoBus = reader.readFloat();

        writer.write("Modelo circular de Buses\n\n" +
                "Media llegada de bus                          " + mediaBus + "\n" +
                "desviación llegada de bus                     " + desviacionBus + "\n" +
                "Media llegada de personas de sus vuelos       " + mediaDeLlegadaPasajerosVuelo + "\n" +
                "Media llegada de personas a los hoteles       " + mediaDeLlegadaPasajerosHotel + "\n" +
                "Fin de la simulación                          " + finDeLaSimulacion+ "\n\n"
        );

        for(int i = 1; i <= 100; i++){

            writer.write("sumlación # " + i + "\n\n" );

            initSimlib();

            Bus1 = new ContinStat("Bus 1",0);
            Bus2 = new ContinStat("Bus 2",0);
            colaHotel1 = new Queue<>("Cola en el hotel 1");
            colaHotel2 = new Queue<>("Cola en el hotel 2");
            colaAereopuerto = new Queue<>("Cola en el Aereopuerto");

            double temp1 = 0;
            double temp2 = salidaSegundoBus;
            int j = 0;
            //Programar todos los movimientos de los buses
            while (temp1 < finDeLaSimulacion || temp2 < finDeLaSimulacion ){
                j++;
                temp1 += Normal(mediaBus, desviacionBus, STREAM_BUS_1);
                temp2 += Normal(mediaBus, desviacionBus, STREAM_BUS_2);

                if( j%3 == 1 ) {
                    if(temp1 < finDeLaSimulacion){
                        eventSchedule(temp1, EVENTO_LLEGADA_BUS1_HOTEL1);
                    }

                    if(temp2 < finDeLaSimulacion){
                        eventSchedule(temp2, EVENTO_LLEGADA_BUS2_HOTEL1);
                    }

                }else if(j%3 == 2 && temp1 < finDeLaSimulacion){

                    if(temp1 < finDeLaSimulacion){
                        eventSchedule(temp1, EVENTO_LLEGADA_BUS1_HOTEL2);
                    }

                    if(temp2 < finDeLaSimulacion){
                        eventSchedule(temp2, EVENTO_LLEGADA_BUS2_HOTEL2);
                    }

                }else {
                    if(temp1 < finDeLaSimulacion){
                        eventSchedule(temp1, EVENTO_LLEGADA_BUS1_AEREOPUERTO);
                    }

                    if(temp2 < finDeLaSimulacion){
                        eventSchedule(temp2, EVENTO_LLEGADA_BUS2_AEREOPUERTO);
                    }
                }

            }

            //programar todas las llegadas de las personas
            temp1 = 0;
            temp2 = 0;
            double temp3 = 0;

            while (temp1 <= finDeLaSimulacion || temp2 <= finDeLaSimulacion || temp3 <= finDeLaSimulacion){
                temp1 += expon(mediaDeLlegadaPasajerosVuelo , STREAM_LEGADA_VUELO);
                temp2 += expon(mediaDeLlegadaPasajerosHotel , STREAM_LEGADA_HOTEL_1);
                temp3 += expon(mediaDeLlegadaPasajerosHotel , STREAM_LEGADA_HOTEL_2);

                if(temp1 <= finDeLaSimulacion){
                    eventSchedule(temp1, EVENTO_LLEGADA_PASAJERO_AEREOPUERTO);
                }
                if(temp2 <= finDeLaSimulacion){
                    eventSchedule(temp2, EVENTO_LLEGADA_PASAJERO_HOTEL1);
                }
                if(temp3 <= finDeLaSimulacion){
                    eventSchedule(temp3, EVENTO_LLEGADA_PASAJERO_HOTEL2);
                }

            }

            eventSchedule(finDeLaSimulacion,EVENTO_FINALIZAR_SIMULACION);

            do {

                /* Determine the next event. */
                timing();

                /* Invoke the appropriate event function. */
                switch (eventType) {

                    case EVENTO_LLEGADA_BUS1_HOTEL1:
                        llegadaBus(1,0);
                        break;
                    case EVENTO_LLEGADA_BUS1_HOTEL2:
                        llegadaBus(1,1);
                        break;
                    case EVENTO_LLEGADA_BUS1_AEREOPUERTO:
                        llegadaBus(1,2);
                        break;
                    case EVENTO_LLEGADA_BUS2_HOTEL1:
                        llegadaBus(2,0);
                        break;
                    case EVENTO_LLEGADA_BUS2_HOTEL2:
                        llegadaBus(2,1);
                        break;
                    case EVENTO_LLEGADA_BUS2_AEREOPUERTO:
                        llegadaBus(2,2);
                        break;
                    case EVENTO_LLEGADA_PASAJERO_HOTEL1:
                        llegadaPasajero(0);
                        break;
                    case EVENTO_LLEGADA_PASAJERO_HOTEL2:
                        llegadaPasajero(1);
                        break;
                    case EVENTO_LLEGADA_PASAJERO_AEREOPUERTO :
                        llegadaPasajero(2);
                        break;
                    case EVENTO_FINALIZAR_SIMULACION:
                        report();
                        break;

                }
                /* If the event just executed was not the end-simulation event (type
                   EVENT_END_SIMULATION), continue simulating.  Otherwise, end the
                   simulation. */
            } while (eventType != EVENTO_FINALIZAR_SIMULACION);;
        }

        writer.close();
        reader.close();
    }

    //tipo de llegada es el tipo de evento y es así hotel1 = 0 hotel2 =1 y aereopuerto = 2
    static void llegadaBus(int numBus,int tipoDeLlegada){
        if (tipoDeLlegada == 0){

            if( numBus == 1 ) Bus1.record( Bus1.getValue() - aeroBus1MidLess );
            if( numBus == 2 ) Bus2.record( Bus2.getValue() - aeroBus2MidLess );


            if(!colaHotel1.isEmpty() && numBus == 1) {
                Bus1.record(Bus1.getValue() + colaHotel1.size());
                colaHotel1.clear();
            }

            if(!colaHotel1.isEmpty() && numBus == 2) {
                Bus2.record(Bus2.getValue() + colaHotel1.size());
                colaHotel1.clear();
            }
        }

        if (tipoDeLlegada == 1){

            if( numBus == 1 ) Bus1.record( Bus1.getValue() - aeroBus1MidMax );
            if( numBus == 2 ) Bus2.record( Bus2.getValue() - aeroBus2MidMax );

            if(!colaHotel2.isEmpty() && numBus == 1){
                Bus1.record(Bus1.getValue() + colaHotel2.size());
                colaHotel2.clear();
            }

            if(!colaHotel2.isEmpty() && numBus == 2){
                Bus2.record(Bus2.getValue() + colaHotel2.size());
                colaHotel2.clear();
            }
        }

        if (tipoDeLlegada == 2){

            if( numBus == 1 ) Bus1.record(0);
            if( numBus == 2 ) Bus2.record(0);

            if(!colaAereopuerto.isEmpty() && numBus == 1){
                Bus1.record( colaAereopuerto.size());
                if( colaAereopuerto.size() % 2 == 0 ){
                    aeroBus1MidLess = (colaAereopuerto.size()/2);
                    aeroBus1MidMax = (colaAereopuerto.size()/2);

                }else {
                    aeroBus1MidLess = (colaAereopuerto.size()/2);
                    aeroBus1MidMax = (colaAereopuerto.size()/2)+1;

                }
                colaAereopuerto.clear();
            }

            if(!colaAereopuerto.isEmpty() && numBus == 2){
                Bus2.record( colaAereopuerto.size());

                if( colaAereopuerto.size() % 2 == 0 ){
                    aeroBus2MidLess = (colaAereopuerto.size()/2);
                    aeroBus2MidMax = (colaAereopuerto.size()/2);

                }else {
                    aeroBus2MidLess = (colaAereopuerto.size()/2);
                    aeroBus2MidMax = (colaAereopuerto.size()/2)+1;

                }
                colaAereopuerto.clear();

            }
        }
    }

    static void llegadaPasajero(int tipo){
        if(tipo == 0){
            colaHotel1.offer(simTime);
        }else if (tipo == 1){
            colaHotel2.offer(simTime);
        }else {
            colaAereopuerto.offer(simTime);
        }

    }

    static void report() throws IOException{
        colaAereopuerto.report(writer);
        colaHotel1.report(writer);
        colaHotel2.report(writer);
        Bus1.report(writer);
        Bus2.report(writer);
    }


}
