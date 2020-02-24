// this is the source code for your bot - have fun!

class ControlFunctionFactory {
    def create = new Bot().respond _
}

class Bot {
    def respond(input: String) = "Status(text=elikiu)"
}
