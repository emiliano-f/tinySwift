class Main{

    static func void main(){
        (IO.out_string("SiVaAFuncionar"));
    }
}

class SubMain: Main{

    private var Array Bool unArrayBool;
    var EstaClaseSiExiste ej;

    init(Object unObjeto){}

    func Array Bool retornarArray(){
        return unArrayBool;
    }
}

class EstaClaseSiExiste: SubMain{

    var Array Bool otroArray;
    func Array Bool retornarArray(Int unEntero){
        return otroArray;
    }
}
