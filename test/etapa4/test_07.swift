class Main{

    static func void main(){

        var A v1;
        var B v2;
        v1 = new B(); //polimorfismo
        v2 = new B();
        //(A.unica()); //error
        //(v2.noPermitidoDesdeObj()); // error
        /*v1.polimorfismo(); //invoca metodo de B
        v1.error(); // error
        v1.unica(); // error*/
        (v2.error(v2)); // polimorfismo
    }
}

class A{
    func void polimorfismo(){

    }
    func void unica(){

    }
}

class B: A{
    func void polimorfismo(){

    }
    func void error(A b1){

    }
    static func void noPermitidoDesdeObj(){

    }
}
