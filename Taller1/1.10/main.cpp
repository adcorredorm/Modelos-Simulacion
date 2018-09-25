#include <iostream>
#include <queue>
#include <cmath>
#include "lcgrand.c"

using namespace std;

//El tiempo de la simulación está dado en segundo, por lo que todas las constantes se encuentran es esta medida

const int DURACION_DE_LA_SIMULACION = 7*3600 , PROMEDIO_PERSONAS_A = 9*60, PROMEDIO_PERSONAS_B = 5*60,
        MEDIA_TRAYECTORIA_BUS = 31*60, DESVIACION_ESTANDAR_TRAYECTORIA_BUS = 5*60;

float reloj, total_tiempo_espera_buses, personas_en_bus,personas_transportadas,viajes_realizados;

int semilla = 1, CAPACIDAD_BUS_N;

bool bus_disponible_A,bus_disponible_B;

queue <int> fila_A;
queue <int> fila_B;

priority_queue< pair<int,int> , vector<pair<int,int>> ,greater<pair<int,int>> > eventos;

pair <int,int > pa;

// lo encontré en la librería de simlib npi sobre como funcione
double Normal(double mi, double sigma)
{
    int i;
    double SUM = 0.0;
    for (i=0; i<12; i++)  SUM += lcgrand(semilla);
    return (SUM-6.0)*sigma + mi;
}


int poisson (int media){
    double L = exp(-media);
    double p = 1.0;
    int k = 0;

    do {
        k++;
        p *= lcgrand(semilla);
    } while (p > L);

    return k - 1;
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
    viajes_realizados = 0.0;

    float temp = 0.0;
    while (temp < DURACION_DE_LA_SIMULACION ){
        temp += poisson(PROMEDIO_PERSONAS_A);
        programar_evento((int)temp,1);
    }

    temp = 0.0;

    while (temp < DURACION_DE_LA_SIMULACION ){
        temp += poisson(PROMEDIO_PERSONAS_B);
        programar_evento((int)temp,2);
    }
}

void salida_bus(int tipo){
    programar_evento((int)reloj + (int)(Normal(MEDIA_TRAYECTORIA_BUS,
                                                 DESVIACION_ESTANDAR_TRAYECTORIA_BUS )),tipo);
    personas_en_bus = 0;
    viajes_realizados++;

}

void llegada_pasajero_A(int hora){
    reloj = hora;
    if(bus_disponible_A and personas_en_bus < CAPACIDAD_BUS_N){
        personas_transportadas++;
        personas_en_bus++;
        if(personas_en_bus == CAPACIDAD_BUS_N){
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
        if(personas_en_bus == CAPACIDAD_BUS_N){
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
            total_tiempo_espera_buses += reloj - fila_B.front();
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
            total_tiempo_espera_buses += reloj - fila_A.front();
            fila_A.pop();
            if (personas_en_bus == CAPACIDAD_BUS_N){
                bus_disponible_A = false;
                salida_bus(3);
                break;
            }
        }
    }
}

void finalizar(){
    while (!fila_A.empty()) {
        total_tiempo_espera_buses += reloj - fila_A.front();
        fila_A.pop();
    }
    while (!fila_B.empty()) {
        total_tiempo_espera_buses += reloj - fila_B.front();
        fila_B.pop();
    }
}


void reporte(){
    cout<<"tiempo de espera total en segundos: "<<total_tiempo_espera_buses<<endl;
    cout<<"La cantidad de viajes realizados fue: "<<viajes_realizados<<endl<<endl;
}


int main() {
    
    float tiempos_espera_medios[100], N_probados[16], total;
    
    for(int j = 0; j <= 15; j++){
        
        CAPACIDAD_BUS_N = j + 5;
        
        for (int i = 1; i <= 100; i++) {
        semilla = i ;
        initialize();
        while (eventos.top().first <= DURACION_DE_LA_SIMULACION){
            //cout<<eventos.top().first<<"    "<<eventos.top().second<<endl;
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
            //cout<<fila_A.size()<<"  "<<fila_B.size()<<"  "<<personas_en_bus<<endl;
            eventos.pop();
        }
        finalizar();
        reporte();
        tiempos_espera_medios[i] = total_tiempo_espera_buses/personas_transportadas;
        }
        total = 0.0;
        for(int k = 0; k < 100; k++) total += tiempos_espera_medios[k];
        N_probados[j] = total/100;
        
        cout << endl;
    }
    
    int min = 0;
    for(int i = 0; i <= 15; i++){
      printf("Tiempos de espera medios N=%i : %1.5f\n", i + 5, N_probados[i]);
      if(N_probados[i] < N_probados[min]) min = i;
    } 
    
    cout << "\n El N minimo es: " << min+5 << endl;

    
    return 0;
}
