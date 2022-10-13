class Main{

    static func void main(){
        var Lexico a1;
        var Array Char a2;

        (Lexico.manager());
        a1 = new Lexico();
        a2 = a1.retornarArray();
    }
}

class Lexico{

    private var Int p1;

    static func void manager(){
        var Lexico b1;
        b1 = new Lexico();
    }

    init(){
    }

    func Array Char retornarArray(){
        var Array Char b2;
        b2 = new Char[p1];
        return b2;
    }
}
