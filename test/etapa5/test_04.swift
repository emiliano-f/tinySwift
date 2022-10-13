// No se puede acceder a una variable de instancia en un contexto estatico - ln: 8

class A {

    var Int a1;
    
    static func void m1(){
        var Int v1;
        v1 = 10 > 11;
    }

}


class B : A{
    static func void m2(){
        (a1);
    }
}


class Main{
    static func void main()
    { }
}


