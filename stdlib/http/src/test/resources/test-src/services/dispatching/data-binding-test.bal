import ballerina/encoding;
import ballerina/http;

listener http:MockListener testEP = new(9090);

type Person record {|
    string name;
    int age;
|};

type Stock record {|
    int id;
    float price;
|};

service echo on testEP {

    @http:ResourceConfig {
        body: "person"
    }
    resource function body1(http:Caller caller, http:Request req, string person) {
        json responseJson = { "Person": person };
        checkpanic caller->respond(<@untainted json> responseJson);
    }

    @http:ResourceConfig {
        methods: ["POST"],
        path: "/body2/{key}",
        body: "person"
    }
    resource function body2(http:Caller caller, http:Request req, string key, string person) {
        json responseJson = { Key: key, Person: person };
        checkpanic caller->respond(<@untainted json> responseJson);
    }

    @http:ResourceConfig {
        methods: ["GET", "POST"],
        body: "person"
    }
    resource function body3(http:Caller caller, http:Request req, json person) {
        json|error val1 = person.name;
        json|error val2 = person.team;
        json name = val1 is json ? val1 : ();
        json team = val2 is json ? val2 : ();
        checkpanic caller->respond(<@untainted> { Key: name, Team: team });
    }

    @http:ResourceConfig {
        methods: ["POST"],
        body: "person"
    }
    resource function body4(http:Caller caller, http:Request req, xml person) {
        string name = <@untainted string> person.getElementName();
        string team = <@untainted string> person.getTextValue();
        checkpanic caller->respond({ Key: name, Team: team });
    }

    @http:ResourceConfig {
        methods: ["POST"],
        body: "person"
    }
    resource function body5(http:Caller caller, http:Request req, byte[] person) {
        string name = <@untainted> encoding:byteArrayToString(person, "UTF-8");
        checkpanic caller->respond({ Key: name });
    }

    @http:ResourceConfig {
        methods: ["POST"],
        body: "person"
    }
    resource function body6(http:Caller caller, http:Request req, Person person) {
        string name = <@untainted string> person.name;
        int age = <@untainted int> person.age;
        checkpanic caller->respond({ Key: name, Age: age });
    }

    @http:ResourceConfig {
        methods: ["POST"],
        body: "person"
    }
    resource function body7(http:Caller caller, http:Request req, Stock person) {
        checkpanic caller->respond();
    }

    @http:ResourceConfig {
        methods: ["POST"],
        body: "persons"
    }
    resource function body8(http:Caller caller, http:Request req, Person[] persons) {
        var jsonPayload = typedesc<json>.constructFrom(persons);
        if (jsonPayload is json) {
            checkpanic caller->respond(<@untainted json> jsonPayload);
        } else {
            checkpanic caller->respond(<@untainted string> jsonPayload.detail().message);
        }
    }
}
