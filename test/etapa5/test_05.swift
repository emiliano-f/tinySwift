// No se puede acceder a una variable de instancia en un contexto estatico - ln: 8

class Main{
    static func void main(){
        var Int x;
        x = 12345;
        (IO.out_int(x));


    }
}
/*
// func void m1(){
// var Int v1;
// v1 = m1(); }
// Error de tipo*/
