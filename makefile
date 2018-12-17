JFLAGS = -g
JC = javac
RM = rm -f
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Proceso.java\
        EchoInterface.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) Proceso.class EchoInterface.class
