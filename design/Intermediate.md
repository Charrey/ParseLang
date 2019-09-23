## Design of the intermediate language of ParseLang

Design limitations:
* The output language is JVM-bytecode, which is class-based. It's wise to make the intermediate language class-based.
* The output language is JVM-bytecode, meaning we cán use arbitrary data structures (set, list) in our intermediate
language (as long as we output this as usage of the right utility class.)

###Proposal:

We create a method for every "Declaration" in ParseLang. This eventually returns a datastructure (list, integer, float,
map, void).

The parameters are Function objects that compute the actual parameter values.

We can keep a stack. Each method entry corresponds with placing a new Nonterminal instance on the stack (that has a
HashMap), while leaving the method corresponds with popping it off the stack.

When an attempt is made to store Y in the nearest parent of Nonterminal N under key X, we travel in the stack from recent
to old. When we find a terminal that corresponds with N, we place <X,Y> in that HashMap, overwriting its current contents

Animals.plang:
```
Farm < Statement = "farm" Animal beast1 Animal' beast2 {
    print("well done");
    print(this["sound"]);
}

Cow < Animal = "cow" {
    print("moo");
}

Sheep < Animal = "sheep" {
    Farm<["sound"] = "BAA";
    print("baah");
}

Start = List params {
    farm cow sheep;
}
```

Corresponds with:
```java
public class Animals {
    
    private Deque<NonTermStorage> storage = new ArrayDeque<>();
    
    private void farm(PLObject beast1, PLObject beast2) {
        System.out.println("well done");
    }
    private void sheep() {
        storage.push(new NonTermStorage("Sheep", new HashMap<PLObject, PLObject>()));
        findFromBack(storage, "Farm", 1).put(PLObject("sound"), PLObject("BAA"));
        System.out.println("baah");
        storage.pop();
    }   

    private void cow() {
        storage.push(new NonTermStorage("Cow", new HashMap<PLObject, PLObject>()));
        System.out.println("moo");
        storage.pop();
    }     

    private void start(PLObject params) {
        storage.push(new NonTermStorage("Start", new HashMap<PLObject, PLObject>()));
        farm(cow(), x --> sheep());
        storage.pop();
    }
 
    
    public static void main(String[] args) {
        Animals instance = new Animals();
        instance.start(new PLObject(Arrays.asList(args)));
    }
}
```

Output:
```
> moo
> well done
ERROR: No key "sound" found for storage of nonterminal Farm at 1:1.
at Farm (1:1)
at Start (15:1)
```
We have to find a suitable **Intermediate** language. Note that we do not have to parse this as we already have the appropriate
objects in memory. In fact, why don't we create a Java object that represents a program?