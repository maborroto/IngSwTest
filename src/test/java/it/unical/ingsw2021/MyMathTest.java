package it.unical.ingsw2021;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import it.unical.ingsw2021.ConnectionManager;
import it.unical.ingsw2021.MyMath;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

//@RunWith(MockitoJUnitRunner.class)
public class MyMathTest {

	private static MyMath myMath;

	// Check the Rule documentation here:
	// https://junit.org/junit4/javadoc/4.12/org/junit/Rule.html

	// Initializes the @Mock before each @Test
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	// connectionManagerMock and httpClientMock will be initialized by Mockito using
	// mock instances once MockitoRule is run
	@Mock
	private ConnectionManager connectionManagerMock;
	@Mock
	private HttpClient httpClientMock;

	// The ExpectedException rule allows you to verify that your code throws a
	// specific exception.
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@BeforeClass
	public static void prepareAll() {
		System.out.println("before class");
		// myMath = new MyMath();
	}

	@AfterClass
	public static void afterClass() {
		System.out.println("after class");
	}

	@Before
	public void prepareTest() {
		System.out.println("before");
		myMath = new MyMath(connectionManagerMock, httpClientMock);
	}

	@After
	public void cleanTest() {
		System.out.println("after");
	}

	@Test(expected = IllegalArgumentException.class)
	public void fibonacciThrowsException() {
		System.out.println("fibonacciThrowsException");
		myMath.fibonacci(-1);
	}

	@Test
	public void fibonacciWorks() {
		System.out.println("testing that fibonacci works");
		assertEquals(0, myMath.fibonacci(0));
		assertEquals(1, myMath.fibonacci(1));
		assertEquals(5, myMath.fibonacci(5));
		assertEquals(8, myMath.fibonacci(6));
		assertEquals(102334155, myMath.fibonacci(40));
	}

	@Test
	public void factorialWorks() {
		System.out.println("testing that factorial works");
		assertEquals(1, myMath.factorial(0));
		assertEquals(120, myMath.factorial(5));
	}

	@Test(timeout = 5000)
	public void fibonacciIsFastEnough() {
		System.out.println("fibonacciIsFastEnough");
		myMath.fibonacci(40);
	}

	/**
	 * Should call connectionManagerMock.isNetworkConnected(),
	 * httpClientMock.sendPost(), and return the number in the last position of the
	 * RESULT array which is the nth number in the Fibonacci series
	 */
	@Test
	public void remoteFibonacciWorks() throws MalformedURLException, IOException {
		System.out.println("testing that remoteFibonacci Works");
		// Inizializziamo il Map di parametri usati per la richiesta online (limit=5)
		// 5 è solo una scelta, potete usare qualsiasi numero, visto che sendPost()
		// ritornerà quello che voremmo noi
		int n = 5;
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("limit", String.valueOf(n));

		// Qui forziamo al metodo connectionManagerMock.isNetworkConnected() a ritornare
		// true quando venga eseguito, evitando un'exception per problemi di
		// collegamento a
		// internet
		when(connectionManagerMock.isNetworkConnected()).thenReturn(true);

		// Qui forziamo il metodo httpClientMock.sendPost() a restituire una stringa con
		// dei risultati:
		// "{\"result\":[\"0\",\"1\",\"1\",\"2\",\"3\",\"5\",\"8\",\"13\",\"21\",\"34\"]}"
		// anyString, any(String.class) e anyMap(), ci servono a indicare che il
		// comportamento che stiamo forzando verrà imposto al metodo quando esso venga
		// eseguito con qualsiasi set di parametri, ovvero sempre in questo caso
		when(httpClientMock.sendPost(anyString(), any(String.class), anyMap()))
				.thenReturn("{\"result\":[\"0\",\"1\",\"1\",\"2\",\"3\",\"5\",\"8\",\"13\",\"21\",\"34\"]}");

		// Eseguiamo il metodo remoteFibbonacci
		BigInteger fib = myMath.remoteFibbonacci(n);

		// Qui verifichiamo che connectionManagerMock.isNetworkConnected() sia stato
		// eseguito una volta, non più non meno
		verify(connectionManagerMock, times(1)).isNetworkConnected();

		// Qui verifichiamo che httpClientMock.sendPost() sia stato eseguito una volta
		// con i parametri giusti (sendPost("fibonacci-numbers", "", queryParameters);)
		// Se remoteFibbonacci viene eseguito con "n=5", allora sendPost() deve
		// essere eseguito passando limit=5
		verify(httpClientMock, times(1)).sendPost("fibonacci-numbers", "", queryParameters);

		// Verifichiamo che la risposta sia quella giusta, ovvero 34, il quale è
		// l'ultimo numero dell'array. Lo stesso array che richiediamo al nostro oggetto
		// mockato
		// (httpClientMock) di restituirci quando viene eseguito sendPost() con
		// qualsiasi set di parametri
		assertEquals(BigInteger.valueOf(34), fib);
	}

	/**
	 * Should call connectionManagerMock.isNetworkConnected(),
	 * httpClientMock.sendPost() that returns an empty string (no HTTP 200 status
	 * code), and then return -1
	 */
	@Test
	public void remoteFibonacciWorkWithEmptyStringResponse() throws MalformedURLException, IOException {
		System.out.println("testing that remoteFibbonacci returns -1 when empty string");
		// Inizializziamo il Map di paramitri usati per la richiesta online (limit=7)
		int n = 5;
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("limit", String.valueOf(n));

		// Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a ritornare
		// true quando venga eseguito, evitando una exception per problemi di
		// collegamento a internet
		when(connectionManagerMock.isNetworkConnected()).thenReturn(true);

		// Qui forziamo il metodo httpClientMock.sendPost() a restituire la stringa
		// "{\"result\":[]}", ovvero array di risultati vuoto
		// anyString, any(String.class) e anyMap(), ci serve ad indicare che il
		// comportamento che stiamo forzando verrà imposto al metodo quando esso venga
		// eseguito con qualsiasi set di parametri, ovvero sempre
		when(httpClientMock.sendPost(anyString(), any(String.class), anyMap())).thenReturn("");

		// Eseguiamo il metodo che stiamo testando
		BigInteger fib = myMath.remoteFibbonacci(n);

		// Qui verifichiamo che connectionManagerMock.isNetworkConnected() sia stato
		// eseguito una volta, non più non meno
		verify(connectionManagerMock, times(1)).isNetworkConnected();

		// Qui verifichiamo che httpClientMock.sendPost() sia stato eseguito una volta
		// con i parametri giusti (sendPost("fibonacci-numbers", "", queryParameters);)
		// Se remoteFibbonacci viene eseguito con "n=5", allora sendPost() deve
		// essere eseguito passando limit=5
		verify(httpClientMock, times(1)).sendPost("fibonacci-numbers", "", queryParameters);

		// Verifichiamo che la risposta sia -1, ovvero la risposta che ci aspettiamo in caso di
		// array di risultati vuoti da parte di sendpost()
		assertEquals(BigInteger.valueOf(-1), fib);
	}

	/**
	 * Should call connectionManagerMock.isNetworkConnected(),
	 * httpClientMock.sendPost() that returns an empty array, and then return -1
	 */
	@Test
	public void remoteFibonacciWorkWithEmptyResults() throws MalformedURLException, IOException {
		System.out.println("testing that remoteFibbonacci returns -1 when empty results array");
		// Inizializziamo il Map di paramitri usati per la richiesta online (limit=7)
		int n = 5;
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("limit", String.valueOf(n));

		// Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
		// true quando venga eseguito, evitando una exception per collegamento a
		// internet
		when(connectionManagerMock.isNetworkConnected()).thenReturn(true);

		// Qui forziamo il metodo httpClientMock.sendPost() a restituirci la stringa
		// "{\"result\":[]}", ovvero array di risultati vuoto
		// anyString, any(String.class) e anyMap(), ci serve a indicare che il
		// comportamento che stiamo forzando verrà imposto al metodo quando esso venga
		// eseguito con qualsiasi set di parametri, ovvero sempre
		when(httpClientMock.sendPost(anyString(), any(String.class), anyMap())).thenReturn("{\"result\":[]}");

		// Eseguiamo il metodo che stiamo testando
		BigInteger fib = myMath.remoteFibbonacci(n);

		// Qui verifichiamo che connectionManagerMock.isNetworkConnected() sia stato
		// eseguito una volta, non più non meno
		verify(connectionManagerMock, times(1)).isNetworkConnected();

		// Qui verifichiamo che httpClientMock.sendPost() sia stato eseguito una volta
		// con i parametri giusti (sendPost("fibonacci-numbers", "", queryParameters);)
		// Se remoteFibbonacci viene eseguito con "n=5", allora sendPost() deve
		// essere eseguito passando limit=5
		verify(httpClientMock, times(1)).sendPost("fibonacci-numbers", "", queryParameters);

		// Verifichiamo che la risposta sia -1, ovvero la risposta che ci aspettiamo in caso di
		// array di risultati vuoti da parte di sendpost()
		assertEquals(BigInteger.valueOf(-1), fib);
	}

	/**
	 * Should call connectionManagerMock.isNetworkConnected(), Should call
	 * httpClientMock.sendPost() that throws IOException due to a certain problem
	 */
	@Test
	public void shouldThrowIOExceptionWhenHttpRequestProblem() throws IOException {
		System.out.println("testing that remoteFibonacci throws IOException when http request problem");
		// Inizializziamo il Map di parametri usati per la richiesta online (limit=7)
		int n = 5;
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("limit", String.valueOf(n));

		// Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
		// true quando venga eseguito, evitando una exception per collegamento a
		// internet
		when(connectionManagerMock.isNetworkConnected()).thenReturn(true);

		// Qui forziamo il metodo httpClientMock.sendPost() a lanciare IOException
		// anyString, any(String.class) e anyMap(), ci serve a indicare che il
		// comportamento che stiamo forzando verrà imposto al metodo quando esso venga
		// eseguito con qualsiasi set di parametri, ovvero sempre
		when(httpClientMock.sendPost(anyString(), any(String.class), anyMap())).thenThrow(IOException.class);

		// Qui indichiamo alla Rule che remoteFibbonacci() deve lanciare IOException
		expectedEx.expect(IOException.class);

		// Eseguiamo il metodo remoteFibbonacci()
		myMath.remoteFibbonacci(n);
	}

	/**
	 * Should call connectionManagerMock.isNetworkConnected(), Should call
	 * httpClientMock.sendPost() that returns a string not in JSON format, an then
	 * throw JSONException
	 */
	@Test
	public void shouldThrowJSONException() throws IOException {
		System.out.println("testing that remoteFibonacci throws JSONException");
		// Inizializziamo il Map di parametri usati per la richiesta online (limit=7)
		int n = 5;
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("limit", String.valueOf(n));

		// Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
		// true quando venga eseguito, evitando un'exception per il collegamento a
		// internet
		when(connectionManagerMock.isNetworkConnected()).thenReturn(true);

		// Qui forziamo il metodo httpClientMock.sendPost() a lanciare IOException
		// anyString, any(String.class) e anyMap(), ci serve a indicare che il
		// comportamento che stiamo forzando verrà imposto al metodo quando esso venga
		// eseguito con qualsiasi set di parametri, ovvero sempre
		when(httpClientMock.sendPost(anyString(), any(String.class), anyMap())).thenReturn("not a JSON");

		// Qui inchiamo alla Rule che remoteFibbonacci() deve lanciare IOException
		expectedEx.expect(JSONException.class);

		// Eseguiamo il metodo remoteFibbonacci()
		myMath.remoteFibbonacci(n);
	}

	/**
	 * Should throws IllegalArgumentException when the parameter is less than 1
	 * 
	 * @throws IOException
	 */
	@Test()
	public void remoteFibbonacciThrowsExceptionWhenWrongParameter() throws IOException {
		System.out.println("testing that remoteFibonacci throws IllegalArgumentException");
		// // Qui indchiamo alla Rule che remoteFibbonacci() deve lanciare
		// IllegalArgumentException
		expectedEx.expect(RuntimeException.class);

		// Qui ci aspettiamo che il messaggio della RuntimeException sia "position index
		// must be greater than 0"
		expectedEx.expectMessage("n index must be greater than 0");

		// Eseguiamo il metodo remoteFibbonacci() con un numero minore di 1
		myMath.remoteFibbonacci(-1);
	}

	/**
	 * Should throws RuntimeException when there is no Internet connection
	 * 
	 * @throws IOException
	 */
	@Test()
	public void remoteFibbonacciThrowsExceptionWhenNotConnection() throws IOException {
		System.out.println("testing that remoteFibonacci throws RuntimeException when no connection");
		// Qui forziamo il metodo connectionManagerMock.isNetworkConnected() a restituirci
		// false quando venga eseguito
		when(connectionManagerMock.isNetworkConnected()).thenReturn(false);

		// Qui indichiamo alla Rule che remoteFibbonacci() deve lanciare
		// RuntimeException
		expectedEx.expect(RuntimeException.class);

		// Qui ci aspettiamo che il messaggio della RuntimeException sia "It's not
		// posible to reach the server"
		expectedEx.expectMessage("There is no internet connection");

		// Eseguiamo il metodo remoteFibbonacci() con qualsiasi parametro perchè
		// la RuntimeException dovrebbe lanciarsi prima
		myMath.remoteFibbonacci(5);
	}

}
