class Derivada : Base{

    var Int x;

    func Int m2(String a){
    }
}

class Base {

    var String s;
    var Int a;

    func void m1(){
    }
}

class Main{

    var Base b1;

    static func void main(){
        b1 = new Derivada();
        (b1.m1());
    }
}
