package base;

import java.util.ArrayList;

public class Algorithm {

	/**
	 * Resuelve la distancia de edición en RAM"
	 * 
	 * @param rows Los valores de las fila inicial de la matriz 
	 * @param cols Los valores de las columna inicial de la matriz<br>
	 * NOTA: El primer valor de la fila y de la columna tiene que representar el mismo valor, para que ambas estén sincronizadas.
	 * @param x El String que representa los caracteres de las columnas
	 * @param y El String que representa los caracteres de las filas
	 * @return Un ArrayList que contiene dos listas: <br> 
	 * la primera una lista de los valores de la última fila calculados por el algoritmo, <br>
	 * la segunda una lista de los valores de la última columna calculados por el algoritmo.
	 */
	public static ArrayList<ArrayList<Integer>> solucionar(ArrayList<Integer> rows, ArrayList<Integer> cols, String x, String y) {
		// Init de datos, para evitar sobre-escribir en los ArrayList iniciales.
		ArrayList<Integer> currentCol = new ArrayList<Integer>(y.length());
		currentCol.addAll(cols);
		ArrayList<Integer> currentRow = new ArrayList<Integer>(x.length());
		currentRow.addAll(rows);
		
		// Para entender el caos
		// fila -> x -> i
		// columna -> y -> j
		
		
		// Itero de izquierda a derecha, arriba a abajo.
		// la fila la voy modificando, así que tengo que guardar la "diagonal", que es el valor anterior de la celda a modificar de la fila.
		Integer diagonal = currentRow.get(0);
		currentCol.set(0, y.length());          // El valor siempre va a ser ese
		
		for(int j = 1; j <= y.length(); j++) {
			currentRow.set(0, currentCol.get(j));  // Fijo el primer valor de la fila como el primero de la columna.
			for(int i = 1; i <= x.length(); i++) {
				/**          
				 *   +----------+--------+   
				 *   | diagonal | row[i] |      (i-1, j-1) | (i, j-1) 
				 *   +----------+--------+      
				 *   | row[i-1] |    ?   |       (i-1 , j ) | (i , j)
				 *   +----------+--------+
				 * 
				 * Como las filas se reescriben, row[i] va a tener 
				 * 
				 */
				Integer value = Math.min(currentRow.get(i) + 1, currentRow.get(i - 1) + 1); // Valores de Arriba, izquierda.
				Integer currentDiag = diagonal + (x.charAt(i-1) == y.charAt(j-1) ? 0 : 1);  // +1 si son distintos los caracteres.
				value = Math.min(value, currentDiag);
				diagonal = currentRow.get(i); // Guardo la diagonal
				currentRow.set(i, value); // Fijo el valor = min( row[i], row[i-1], diagonal + (1 ó 0) )
			}
			diagonal = currentCol.get(j); // Al terminar una fila completa, la diagonal va a ser el valor de la columna actual.
			currentCol.set(j, currentRow.get(x.length())); // La reemplazo por el último valor de la fila.
		}
		
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		// Así los resultados son más accesibles... Pero usar ArrayLists ensucian un poco la visibilidad del código...
		result.add(currentRow);
		result.add(currentCol);
		return result;
	}
	
	
	/**
	 * Calcula una iteración usando sólo un valor de columna, y una fila.
	 * @param row
	 * @param colValue
	 * @param x
	 * @param y
	 * @return La fila completa modificada.
	 */
	public static ArrayList<Integer> solucionar(ArrayList<Integer> row, int colValue, String x, String y){
		ArrayList<Integer> col = new ArrayList<Integer>(2);
		// Por la nota que está en el javadoc de solucionar que está arriba, se debe agregar ese valor a la columna.
		col.add(row.get(0));
		col.add(colValue);
		
		ArrayList<ArrayList<Integer>> partialSol = solucionar(row, col, x, y);
		return partialSol.get(0);
	}
	
	/**
	 * Azucar sintactico para utilizar realizar la solución básica
	 * @param x String que representa los caracteres de las columnas
	 * @param y String que representa los caracteres de las filas
	 * @return
	 */
	public static ArrayList<ArrayList<Integer>> solucionar(String x, String y){
		ArrayList<Integer> cols = new ArrayList<Integer>();
		ArrayList<Integer> rows = new ArrayList<Integer>();
		
		for(int i = 0; i <= x.length(); ++i)
			rows.add(i);

		for(int i = 0; i <= y.length(); ++i)
			cols.add(i);
				
		return solucionar(rows, cols, x, y);
	}
}
