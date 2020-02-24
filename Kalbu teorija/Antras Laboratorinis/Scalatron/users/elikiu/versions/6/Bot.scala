// Tutorial Bot #2: Counting Cycles

class ControlFunction {
    var n = 0
    def respond(input: String) = {
        val output = "Status(text=" + n + ")"   // temp value
        n += 1
        output                                  // yield
    }
}

class ControlFunctionFactory {
    def create = new ControlFunction().respond _
}

