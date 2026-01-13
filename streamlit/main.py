# Punto de entrada al programa
import AplicacionSentimientos

if __name__ == "__main__":
    alpha = AplicacionSentimientos.AplicacionSentimientos() 
    alpha.ConfigurarModelos()
    alpha.EjecutarInterfaz()