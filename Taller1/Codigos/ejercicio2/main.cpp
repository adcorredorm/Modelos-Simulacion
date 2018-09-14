#include <iostream>
#include <queue>
#include "lcgrand.c"

using namespace std;

//El tiempo de la simulación está dado en segundo, por lo que todas las constantes se encuentran es esta medida
//Solo me falta poner bien las distribuciones

const int DURACION_DE_LA_SIMULACION = 28800 , CAPACIDAD_BUS_N = 10, PROMEDIO_PERSONAS_A = 540, PROMEDIO_PERSONAS_B = 300,
        MEDIA_TRAYECTORIA_BUS = 1860, DESVIACION_ESTANDAR_TRAYECTORIA_BUS = 300;

float reloj, total_tiempo_espera_buses, personas_en_bus,personas_transportadas;

bool bus_disponible_A,bus_disponible_B;

queue <int> fila_A;
queue <int> fila_B;

priority_queue< pair<int,int> , vector<pair<int,int>> ,greater<pair<int,int>> > eventos;

pair <int,int > pa;


float uniforme(int a, int b, int semilla) {
    return a + lcgrand(semilla) * (b - a);
}

void programar_evento(int tiempo, int tipo){
    pa.first = tiempo;
    pa.second = tipo;
    eventos.push(pa);

}

void initialize() {

    reloj = 0.0;

    bus_disponible_A = true;
    bus_disponible_B = false;

    personas_transportadas = 0.0;
    total_tiempo_espera_buses = 0.0;
    personas_en_bus = 0.0;

    float temp = 0.0;
    while (temp < DURACION_DE_LA_SIMULACION ){
        temp += (uniforme(PROMEDIO_PERSONAS_A - 20, PROMEDIO_PERSONAS_A + 20 , 1));
        programar_evento((int)temp,1);
    }

    temp = 0.0;

    while (temp < DURACION_DE_LA_SIMULACION ){
        temp += (uniforme(PROMEDIO_PERSONAS_B - 20, PROMEDIO_PERSONAS_B + 20 , 1));
        programar_evento((int)temp,2);
    }
}

void salida_bus(int tipo){
    programar_evento((int)reloj + (int)(uniforme(MEDIA_TRAYECTORIA_BUS - DESVIACION_ESTANDAR_TRAYECTORIA_BUS,
                                                 MEDIA_TRAYECTORIA_BUS + DESVIACION_ESTANDAR_TRAYECTORIA_BUS , 1)),tipo);
    personas_en_bus = 0;

}

void llegada_pasajero_A(int hora){
    reloj = hora;
    if(bus_disponible_A and personas_en_bus < CAPACIDAD_BUS_N){
        cout<<"pasó por aquí"<<endl;
        personas_transportadas++;
        personas_en_bus++;
        if(personas_en_bus == 10){
            bus_disponible_A = false;
            salida_bus(3);

        }
    }else{
        fila_A.push(hora);

    }
}

void llegada_pasajero_B(int hora){
    reloj = hora;
    if(bus_disponible_B and personas_en_bus < CAPACIDAD_BUS_N){
        cout<<"pasó por aquí"<<endl;
        personas_transportadas++;
        personas_en_bus++;
        if(personas_en_bus == 10){
            bus_disponible_B = false;
            salida_bus(4);
        }
    }else{
        fila_B.push(hora);

    }
}

void llegar_a_la_estacion_B(int hora){
    reloj = hora;
    bus_disponible_B = true;
    if(!fila_B.empty()){
        while (!fila_B.empty()) {
            personas_en_bus++;
            total_tiempo_espera_buses = reloj - fila_B.front();
            fila_B.pop();
            if (personas_en_bus == CAPACIDAD_BUS_N){
                bus_disponible_B = false;
                salida_bus(4);
                break;
            }
        }
    }
}

void llegar_a_la_estacion_A(int hora){
    reloj = hora;
    bus_disponible_A = true;
    if(!fila_A.empty()){
        while (!fila_A.empty()) {
            personas_en_bus++;
            total_tiempo_espera_buses = reloj - fila_A.front();
            fila_A.pop();
            if (personas_en_bus == CAPACIDAD_BUS_N){
                bus_disponible_A = false;
                salida_bus(3);
                break;
            }
        }
    }
}

void reporte(){
    cout<<total_tiempo_espera_buses;
}

int main() {
    initialize();
    while (eventos.top().first <= DURACION_DE_LA_SIMULACION){
        cout<<eventos.top().first<<"  "<<eventos.top().second<<endl;
        cout<<fila_A.size()<<endl;
        cout<<fila_B.size()<<endl;
        cout<<personas_en_bus<<endl;
        switch (eventos.top().second){
            case 1:
                llegada_pasajero_A(eventos.top().first);
                break;
            case 2:
                llegada_pasajero_B(eventos.top().first);
                break;
            case 3:
                llegar_a_la_estacion_B(eventos.top().first);
                break;
            case 4:
                llegar_a_la_estacion_A(eventos.top().first);
                break;
            default:
                break;
        }
        eventos.pop();
    }
    reporte();
    return 0;
}