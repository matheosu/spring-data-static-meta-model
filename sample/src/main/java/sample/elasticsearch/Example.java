package sample.elasticsearch;

public class Example {

    public static void main(String[] args) {

        System.out.println(Customer_.name.get());
        System.out.println(Customer_.birthDate.get());
        System.out.println(Customer_.address.get());
        System.out.println(Customer_.address.get(Address_.state));
        System.out.println(Customer_.address.get(Address_.street));
        System.out.println(Customer_.address.get(Address_.number));
        System.out.println(Customer_.address.get(Address_.zipCode));



    }
}
