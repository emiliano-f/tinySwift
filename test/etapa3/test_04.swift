class Figura{

    private var Array Int figuras;
    private var Int count;

    init(){
        count = 0;
    }

    static func Int counter(){
        return count;
    }

    static func void addCounter(Int a){
        count = count + a;
    }

    func Array Int getFiguras(){
        return self.figuras;
    }

}

class Cuadrado: Figura{

    var Int lados;
    init(){
        (Figura.addCounter(1));
    }
}

class Main{

    static func void main(){
        var Figura unaFigura;
        unaFigura = new Cuadrado();
    }
}
