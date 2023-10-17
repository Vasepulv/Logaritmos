package base;

// import java.util.Iterator;
import java.util.Random;


/**
 * Esto ya no es una copia
 */
public class StringGen {
	private int baseSeed;
		
	public StringGen() {
		// La semilla base asegura que se guarden los caracteres.
		Random rng = new Random();
		baseSeed = rng.nextInt();
	}
	
	/**
	 * Retorna el caracter que está en el lugar dado.
	 * @param value Posición para pedir.
	 * @return
	 */
	public char getCharAt(int value) {
		Random rng = new Random(baseSeed + value);
		return (char)(rng.nextInt() % 256);
	}
	
	/**
	 * Retorna un SubString del StringGen <br>
	 * Es equivalente (y un poco más eficiente) que pedir getCharAt size veces.
	 * @param start  Desde qué punto se quiere pedir
	 * @param size El tamaño del string, que se quiere retornar.
	 * @return
	 */
	public String getSubString(int start, int size) {
		Random rng = new Random();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < size; i++) {
			rng.setSeed(baseSeed + start + i);
			stringBuilder.append((char)(rng.nextInt() % 256));
		}
		return stringBuilder.toString();
	}
	
	/*
	static final Iterable<Character> alphabet() {
		  return new Iterable<Character>() {

		    private final char[] ALPHA = new char[] {
		        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
		    };

		    public final Iterator<Character> iterator() {
		      return new Iterator<Character>() {

		        private int cursor;

		        public boolean hasNext() {
		          return true;
		        }

		        public Character next() {
		          char ch = ALPHA[cursor];
		          cursor = (cursor + 1) % 26;
		          return ch;
		        }

		        public void remove() {
		          throw new UnsupportedOperationException("cannot remove from stream");
		        }
		      };
		    }
		  };
		}
	*/
}
