import ballerina/io;
import ballerina/lang.'object;

function __init() {
	io:println("Initializing module a");
}

public function main() {
}

public type ABC object {

    *'object:AbstractListener;
    private string name = "";

    public function __init(string name){
        self.name = name;
    }

    public function __start() returns error? {
        io:println("a:ABC listener __start called, service name - " + self.name);
        if (self.name == "ModB") {
            error sampleErr = error("error returned while starting module B");
            return sampleErr;
        }
    }

    public function __gracefulStop() returns error? {
        io:println("a:ABC listener __gracefulStop called, service name - " + self.name);
        return ();
    }

    public function __immediateStop() returns error? {
        io:println("a:ABC listener __immediateStop called, service name - " + self.name);
        return ();
    }

    public function __attach(service s, string? name = ()) returns error? {
        io:println("a:ABC listener __attach called, service name - " + self.name);
    }

    public function __detach(service s) returns error? {
        io:println("a:ABC listener __detach called, service name - " + self.name);
    }
};

listener ABC ep = new ABC("ModA");
