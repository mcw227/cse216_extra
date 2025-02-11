## HOW TO USE THIS CLASS
When prompted for the URI, you should put something similar to "http://localhost:[portnumber]/messages". In other words, it should match the basic http calls we used in the tutorial.<br>

That being said, this class **only works with the tutorial backend by default** but hopefully the documentation is such that you may alter the methods to work with any javalin/http request server! <br><br>
## THINGS THAT COULD BE IMPROVED
- This implementation is single class focused so it uses HttpClient/Request/Response classes instead of maven dependencies like Apache which may be more standardized.
- Right now it is hard coded to work with the http calls of the tutorial class, it would be better if it was more flexible
- A lot of repetition when it comes to http request creation, this could probably be reworked into a function call.
