# Blockchain en Java
Es una blockchain basica implementando PoW (Proof-of-work), dejando una blockchain funcional de forma local, logrando hacer transacciones de una wallet a ortra y validando si se puede realizar la transacción.

Este ejemplo (de uso personal) solo es para poder entender como implementar una blockchain
  

(al ejecutar con consola, sin el uso de un IDE)  

**compilar:** `javac -cp .;gson-2.6.2.jar;bcprov-jdk15on-159.jar BlockChain.java`  

**ejecutar:** `java -cp .;gson-2.6.2.jar;bcprov-jdk15on-159.jar BlockChain`  


**KeyPairGenerator**. Se emplea para crear claves públicas y privadas.  

**MessageDigest**. Prorciona la funcionalidad de algoritmos de resumen de mensajes como el MD5 y el SHA.  

**Signature**. Se emplea para el firmado digital de mensajes.  
