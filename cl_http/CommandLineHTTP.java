import java.util.Scanner;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * There is another built in library for HTTP requests
 * However, it seems like this method is more modern
 * If I remember correctly, we will use Apache in this class.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class provides an easy way to make http calls and disect their responses without writing repetitive curl lines.
 * Note that due to the unique nature of http calls, this only works for the class we made using the tutorial.
 * Note also that the URI provided must have /messages or whatever the actual database url is
 */
public class CommandLineHTTP {

    /**
     * BEGIN STATIC FIELD DEFINITIONS
     * These are unchanging status parameters and messages
     */
    private static Scanner in = new Scanner(System.in);

    private static final String INPUT_ERR = "Please provide correctly formatted input.";

    private static final String URI_ERR = "Error occured when setting URI";
    private static final int URI_ERRCODE = -3;

    private static final String CONNECTION_ERR = "Error querying connection";
    private static final int CONNECTION_ERRCODE = -4;

    private static final String DEFAULT_ERR = "An unknown error has occured. Sorry!";

    private static final String JSON_FORM = "{'mTitle':'%s', 'mMessage':'%s'}";
    /**
     * END STATIC FIELD DEFINITIONS
     */

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("URI? ");
        String uri_string = in.nextLine();

        try { URI uri = new URI(uri_string); } //Automatic test to see if the URI is valid by the constructor function
        catch (URISyntaxException e) {
            e.printStackTrace(); 
            handleErr("URI");
        }

        boolean quit = false;
        while(quit != true) {
            printMenu();
            String choice = in.nextLine();
            switch (choice) {
                case "G1":
                    getOne(uri_string,client);
                    break;
                case "GA":
                    getAll(uri_string,client);
                    break;
                case "P":
                    postOne(uri_string,client);
                    break;
                case "D":
                    deleteOne(uri_string,client);
                    break;
                case "Q":
                    quit = true;
                    break;
            }
        }
        System.out.println("Bye!"); 
    }

    public static void printMenu() {
        System.out.println("----Available Options:----\n"+
        "G1: Get One\n" +
        "GA: Get All\n" +
        "P: Post One\n" +
        "D: Delete One\n" +
        "Q: Quit\n\n\n");
    }

    /**
     * Sends a "get one" request to the server
     * @param uri URL to query
     * @param client HttpClient object used to handle the request
     */
    public static void getOne(String uri, HttpClient client) {
        System.out.print("GET which row id?: ");
        int id = 0;
        try {id = in.nextInt(); } catch (Exception e) { handleErr("Input"); return;}
        String pathURL = uri + "/" + id;
        try {
            System.out.println("Trying following request: " + pathURL + " GET ");
            HttpRequest pathRequest = HttpRequest.newBuilder()
            .uri(new URI(pathURL))
            .GET()
            .build();

            try { 
                HttpResponse<String> pathResponse = client.send(pathRequest, HttpResponse.BodyHandlers.ofString());
                printHttp(pathResponse); //print outcome
            }
            catch (Exception e) { //Possible errors include connection interruption and IO exception
                e.printStackTrace();
                handleErr("Connection"); //Fatal error, will exit
            }
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            handleErr("URI");
        }
        return;
    }

    /**
     * Sends a "get one" request to the server
     * @param uri URL to query
     * @param client HttpClient object used to handle the request
     */
    public static void getAll(String uri, HttpClient client) {
        String pathURL = uri;
        try { //Is there a better way to build the request?
            System.out.println("Trying following request: " + pathURL + " GET ");
            HttpRequest pathRequest = HttpRequest.newBuilder()
            .uri(new URI(pathURL))
            .GET()
            .build();

            try { 
                HttpResponse<String> pathResponse = client.send(pathRequest, HttpResponse.BodyHandlers.ofString());
                printHttp(pathResponse); //print outcome
            }
            catch (Exception e) { //Possible errors include connection interruption and IO exception
                e.printStackTrace();
                handleErr("Connection"); //Fatal error, will exit
            }
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            handleErr("URI");
        }
        return;
    }

    /**
     * Sends a "POST" request to the server
     * @param uri URL to query
     * @param client HttpClient object used to handle the request
     */
    public static void postOne(String uri, HttpClient client) {
        String pathURL = uri;

        System.out.print("Title? ");
        String title = in.nextLine();
        System.out.print("Message? ");
        String message = in.nextLine();

        //Can be changed to whatever json is desired up above
        String json = String.format(JSON_FORM, title, message);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(new URI(pathURL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)) //attach the body we created above
                .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            printHttp(resp);
        } catch (Exception e) {
            e.printStackTrace();
            handleErr("Connection");
        }

    }

    /**
     * Send a one row delete request
     * @param uri The URL to query
     * @param client The HttpClient object used to handle the request
     */
    public static void deleteOne(String uri, HttpClient client) {
        System.out.print("DELETE which row id?: ");
        int id = 0;
        try {id = in.nextInt(); } catch (Exception e) { handleErr("Input"); return;}
        String pathURL = uri + "/" + id;
        try {
            System.out.println("Trying following request: " + pathURL + " GET ");
            HttpRequest pathRequest = HttpRequest.newBuilder()
            .uri(new URI(pathURL))
            .DELETE()
            .build();

            try { 
                HttpResponse<String> pathResponse = client.send(pathRequest, HttpResponse.BodyHandlers.ofString());
                printHttp(pathResponse); //print outcome
            }
            catch (Exception e) { //Possible errors include connection interruption and IO exception
                e.printStackTrace();
                handleErr("Connection"); //Fatal error, will exit
            }
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            handleErr("URI");
        }
        return;
    }

    /**
     * To avoid rewriting print code
     * @param pathResponse The HttpResponse to be printed
     */
    public static void printHttp(HttpResponse<String> pathResponse) {
        System.out.println("Response:___________\nStatus:" + 
        pathResponse.statusCode() +
        "\nBody:" + pathResponse.body() + "\n\n\n");
    }
    
    /**
     * Handles errors, also decides which cause the program to exit or not.
     * @param type The type of error found.
     */
    public static void handleErr(String type){
        switch (type) {
            case "Input": //non fatal, announcement only
                System.err.println(INPUT_ERR);
            case "URI": //fatal
                System.err.println(URI_ERR);
                System.exit(URI_ERRCODE);
            case "Connection": //fatal
                System.err.println(CONNECTION_ERR);
                System.exit(CONNECTION_ERRCODE);
            default:
                System.err.println(DEFAULT_ERR);
        }
    }
}