 
class Fibonacci {
private var Int suma;
private var Int i,j;
func Int sucesion_fib(Int n){
i=0; j=0; suma=0;
while (i<= n){
if (i==0){
(imprimo_numero(i));
(imprimo_sucesion(suma));
}
else if(i==1){
    (imprimo_numero(i));
    suma=suma+i;
    (imprimo_sucesion(suma)         )       ;  //
}
else{
(imprimo_numero(i));
suma    = suma+j;
j=suma;
(imprimo_sucesion(suma));
}
}
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
