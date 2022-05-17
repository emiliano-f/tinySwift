class Main(){


}

class Fibonacci {
    private var Int suma;
    private var Int i,j;

    func Int sucesion_fib(Int n){
        i=0; j=0; suma=0;
    }

    init(){
        i=0;
        j=0;
        suma=0;
    }

    func void imprimo_numero(Int num) {
        (IO.out_string("f_"));
        (IO.out_int(num));
        (IO.out_string("="));
    }

    func void imprimo_sucesion(Int s) {
        //"el valor es: ";
        (IO.out_int(s));
        (IO.out_string("\n"));
    }
}

class Main{
    static func void main(){
        var Fibonacci fib;
        var Int n;
        fib = new Fibonacci();
        n = IO.in_int();
        (fib.sucesion_fib(n));
    }
}

class UnaClase: Fibonacci{

}
