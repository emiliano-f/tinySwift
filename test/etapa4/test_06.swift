class Main{

    var Char char;

    static func void main(){
        var Lexico a1;
        var Array Char a2;
        var Sintactico a3;

        (Lexico.manager());
        a1 = new Lexico();
        a2 = a1.retornarArray();
        a3 = new Sintactico();
        a1 = a3.retornarLexico();
        a2 = a3.retornarArray();
        (self.modificarValor(a2, true));
    }

    func void modificarValor(Array Char a2, Bool flag){
        var Array Int y1;
        if (flag){
            char = a2[2];
            y1 = new Int[1];
        }
        else{
            a2 = nil;
        }
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

class Sintactico: Lexico{
    func Lexico retornarLexico(){
        var Lexico c1;
        return new Lexico();
    }
}
