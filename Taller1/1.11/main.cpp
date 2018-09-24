#include <iostream>
#include <math.h>
//#include "lcgrand.h"
#include <stdlib.h>
#include <time.h>
using namespace std;

const int CANT_REPARADORES = 1, DIST_COBRO = 5, DIST_REPARACION = 3, DIST_DEMORA_REP = 5*60, MAX_FILA = 1000; //Usar max fila para no ponernos a usar listas xD
  
const float TIEMPO_SIMULADO = 8.0*3600.0, DIST_CLIENTES = 3600.0/8.0, TIEMPO_GARANTIA = 3600;

int tipo_siguiente_evento, largo_fila, clientes_que_llegan, clientes_atendidos, clientes_que_no_pagan, reparadores_ocupados;

float reloj, tiempo_ultimo_evento, beneficios, area_fila, area_reparadores_ocupados, total_tiempo_espera, 
    eventos[2], llegada_clientes[MAX_FILA], clientes_en_reparacion[CANT_REPARADORES], cobro, costo;

bool cobro_reparador[CANT_REPARADORES]; //Si true cobra al siguiente cliente

int semilla;

float exponencial(float media, int semilla){
    
    return -media * log(drand48());
}


void initialize(){
  
  srand48(semilla);

  reloj = 0.0;

  for(int i = 0; i < CANT_REPARADORES; i++) cobro_reparador[i] = true;
  tiempo_ultimo_evento = 0.0;

  beneficios = 0.0;
  clientes_que_llegan = 0;
  clientes_atendidos = 0;
  clientes_que_no_pagan = 0;
  reparadores_ocupados = 0;
  area_fila = 0.0;
  area_reparadores_ocupados = 0.0;
  total_tiempo_espera = 0.0;
  largo_fila = 0;
  for(int i = 0; i < CANT_REPARADORES; i++) llegada_clientes[i] = 0.0;
  

  eventos[0] = exponencial(DIST_CLIENTES, semilla);
  eventos[1] = 1.0e+30;
  for(int i = 0; i < CANT_REPARADORES; i++) clientes_en_reparacion[i] = 1.0e+30;
}


void timing(){
  float min = 1.0e+29;
  tipo_siguiente_evento = -1;

  for(int i = 0; i < 2; i++){
    if(eventos[i] < min){
      tipo_siguiente_evento = i;
      min = eventos[i];
    }
  }

  if(tipo_siguiente_evento < 0){
    cout << "No se encontro evento" << endl;
    exit(1);
  }

  reloj = eventos[tipo_siguiente_evento];
}

void update_stats(){

  float ancho = reloj - tiempo_ultimo_evento; //Se llama asi porque es el ancho del dichoso rectangulo :V
  tiempo_ultimo_evento = reloj;

  area_fila += largo_fila * ancho;
  area_reparadores_ocupados += reparadores_ocupados * ancho;

}


void llegada_cliente(){

  eventos[0] = reloj + exponencial(DIST_CLIENTES, semilla+1);
  clientes_que_llegan++;
  
  if(reparadores_ocupados < CANT_REPARADORES){
    
    int reparador_disponible = -1;
    
    for(int i = 0; i < CANT_REPARADORES; i++){
      if(clientes_en_reparacion[i] > 1.0e+29){
        reparador_disponible = i;
        break;
      }
    }
    
    if(reparador_disponible < 0){
      cout << " Error para encontrar un reparador" << endl;
      exit(1);
    }
    
    cobro_reparador[reparador_disponible] = true;
    clientes_en_reparacion[reparador_disponible] = reloj + exponencial(DIST_DEMORA_REP, semilla+2);
    
    if(clientes_en_reparacion[reparador_disponible] < eventos[1])
        eventos[1] = clientes_en_reparacion[reparador_disponible];
    
    reparadores_ocupados++;
      
  }else{
    
    if(largo_fila < MAX_FILA){
      
        llegada_clientes[largo_fila] = reloj;
        largo_fila++;
        
    }else{
        cout << clientes_que_llegan <<" Hay que subirle al maximo Fila" << endl;
        exit(1);
    }
    
  }

}

void despacho_cliente(){
  
  clientes_atendidos++;
  
  int reparador_actual = -1;

  for(int i = 0; i < CANT_REPARADORES; i++){
    if(clientes_en_reparacion[i] == reloj){
      reparador_actual = i;
      break;
    }
  }

  if(reparador_actual < 0){
    cout << "Error con la asignacion de reparadores" << endl;
    exit(1);
  }
    
  cobro = exponencial(DIST_COBRO, semilla+3);
  costo = exponencial(DIST_REPARACION, semilla+4);
  
  if(cobro_reparador[reparador_actual]) beneficios += cobro;
  else clientes_que_no_pagan++;
  beneficios -= costo;
  
  if(largo_fila > 0){
    
    float espera = reloj - llegada_clientes[0];
    
    total_tiempo_espera += espera;
    
    cobro_reparador[reparador_actual] = espera < TIEMPO_GARANTIA? true:false;
    clientes_en_reparacion[reparador_actual] = reloj + exponencial(DIST_DEMORA_REP, semilla+5);
    
    largo_fila--;
    for(int i = 0; i < largo_fila; i++){
      llegada_clientes[i] = llegada_clientes[i+1];
    }
    
  }else{
    clientes_en_reparacion[reparador_actual] = 1.0e+30;
    reparadores_ocupados--;
  }
  
  float min = 1.0e+30;
    for(int i = 0; i < CANT_REPARADORES; i++){
      if(clientes_en_reparacion[i] < min) min = clientes_en_reparacion[i];
    }
    eventos[1] = min;
 
}


void generar_estadisticas(){
  cout << "Tiempo Simulado: " << reloj << endl;
  cout << "Clientes Atendidos: " << clientes_atendidos << endl;
  cout << "Tiempo espera medio: " << total_tiempo_espera/clientes_atendidos << endl;
  cout << "Proporcion de personas que no pagaron: " << (double)clientes_que_no_pagan/clientes_atendidos << endl;
  cout << "Beneficios Totales: " << beneficios << endl;
  cout << "Beneficio medio por cliente: " << (double)beneficios/clientes_atendidos << endl << endl;
}


int main(){

  for(int i = 0; i < 10; i++){

    semilla = (int)(drand48()*100) + 1;

    initialize();

    while(reloj < TIEMPO_SIMULADO){
      timing();

      update_stats();

      switch(tipo_siguiente_evento){
        case 0:
          llegada_cliente();
          break;
        case 1:
          despacho_cliente();
          break;
      }
    }

    generar_estadisticas();
  }


  return 0;
}

/* g++ -Wall principal.cpp biblioteca1.cpp -o salida //Para compilar
  https://plot.ly/create/box-plot/#/    //Para hacer el box
 */
