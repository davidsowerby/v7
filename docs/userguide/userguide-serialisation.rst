=============
Serialization
=============

When Vaadin serialises to the session, it serialises the entire UI. This
means anything contained within the UI is also serialized. If you follow
the Krail approach of constructor injection for Views and UIs, it will
mean that those dependencies will also be serialized, unless, of course,
they are marked as **transient**.

This clearly could affect the amount that needs to be
serialized/deserialized - you may want to reduce that by making
dependencies **transient** (or you may just have dependencies which
cannot be serialized), but that in turn means you need a way to
reconstruct the **transient** fields.

Serialization and Shiro / JPA
=============================

Anything which uses Guice AOP generates a byte enhanced class produced
by cglib. This causes serialization problems, and is a feature of
anything which uses cglib. At the moment the only way round this is to
use manual coding instead of AOP supported annotations. For example,
instead of using:

.. sourcecode:: java

    @RequiresPermission()
    public void doSomething(){

    }

use

.. sourcecode:: java

    if (subjectProvider.get().isPermitted("page:view:private")) {
        userNotifier.notifyInformation(LabelKey.Yes);
    }

There is an `open
issue <https://github.com/KrailOrg/krail/issues/686>`__ to provide more
support.

Guice Deserialization for View and UI instances
===============================================

``ViewBase`` and ``ScopedUI`` use ``SerializationSupport`` to make the
management of this situation simpler, designed in a way for sub-classes
to make use of this facility.

When instances or sub-classes of ``ViewBase`` and ``ScopedUI`` are
deserialized, a standard Java ``readObject()`` is invoked method, and
``SerializationSupport`` used to re-inject **transient** fields using
the Guice Injector. Hooks are also provided to allow you to intervene
with your own logic at various points.



.. caution::    To enable this to work, certain conditions apply. Sub-classes of ``ViewBase`` and ``ScopedUI`` :

    -  must have non-Serializable fields must be marked **transient**,
       as normal

    -  will only attempt to re-inject transient fields which have a null
       value at the time it invokes
       ``SerializationSupport.injectTransientFields()`` - see the call
       sequence below

    -  must have an exact match between the type of the constructor
       parameter and the type of the field that it is associated with

    -  will raise an exception if, after completing the sequence of
       calls below, there are still null **transient** fields

Call Sequence
-------------

This is the sequence of calls made during deserialization. Note that
*injection by SerializationSupport* will only inject into null
**transient** fields

-  beforeDeserialization()

-  *default deserialization*

-  beforeTransientInjection()

-  *SerializationSupport injects transients*

-  afterTransientInjection()

-  *SerializationSupport checks for null transients*, and raises
   exception if any found (unless excluded)

Matching constructor parameters with fields
-------------------------------------------

In order to match a constructor parameter with its field for automatic
re-injection, they must both be of exactly the same type (and not just
assignable). In the case of Guice, the type includes the use of binding
annotations.

This means that where a binding annotation is used on a constructor
parameter, its associated field must also have the same binding
annotation.

Java example
~~~~~~~~~~~~

In Java, we must annotate the field to match a constructor parameter
that uses a binding annotation. Your IDE may flag a warning that you
have a binding annotation without @Inject - this can be ignored /
suppressed. If you do annotate the field with @Inject, then outside of
deserialization, Guice will inject the field twice, once via the
constructor, and once directly to the field.

.. sourcecode:: java

    public class MyView extends ViewBase {

       @Named("1)  // to match its constructor parameter
       private transient Widget widget1;

       @Inject
       protected MyView(Translate translate, SerializationSupport serializationSupport, @Named("1") Widget widget1){
          super(translate, serializationSupport)
          this.widget1=widget1
       }

    }

Kotlin example
~~~~~~~~~~~~~~

Because Kotlin declares a property rather than a separate constructor
parameter and field, the property needs to be annotated in a way that
causes Kotlin’s code generator to correctly annotate its Java output:

.. sourcecode:: kotlin

    class MyView @Inject constructor(translate:Translate, serializationSupport:SerializationSupport, @field:Named("1") @param:Named("1") @Transient val widget1:Widget) : ViewBase(translate,serializationSupport)

Excluding fields
----------------

If for some reason you want a transient field to be null at the end of
the deserialization process, fields can be excluded from injection and
the final check, by overriding the ``ViewBase`` or ``ScopedUI`` method
``beforeDeserialization()`` or ``beforeTransientInjection()`` to set the
exclusions

.. sourcecode:: java

    protected void beforeTransientInjection(){
       serializationSupport.setExcludedFieldNames(ImmutableList.of("thisField"));
    }



.. tip::    **Guice, Binding Annotations and Inheritance**. There is an
    "interesting" side effect from using Guice binding annotations. It
    is very easy to provide the binding on a superclass constructor
    parameter, and then forget to put it on the equivalent sub-class
    constructor parameter - meaning you have injected something
    different via the sub-class. Your IDE and compiler will not tell
    you. This Serialization routine will tell you if you do so. This was
    not really a design choice, just a bit of luck!

Non-Serializable classes
========================

This list is not exhaustive, but identifies some of the commonly used
Krail classes which cannot be made Serializable. For these, use the
method described above to re-inject them.

-  ``BusProvider`` implementations which use MBassador. This currently  applies to all ``BusProvider`` implementations.

-  ``PubSubSupport`` from MBassador

Making your classes 'Guice Serializable'
========================================

Constructed directly by Guice
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Where you need to deserialize your own classes that are constrcuted by Guice, but has non-Serializable dependencies, you can still use ``SerializationSupport``,
within the standard ``readObject()`` deserialization method:

.. sourcecode:: java
   :caption: Java

    private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
        inputStream.defaultReadObject();
        serializationSupport.deserialize(this);
    }

.. sourcecode:: kotlin
   :caption: Kotlin

   @Throws(ClassNotFoundException::class, IOException::class)
   private fun readObject(inputStream: ObjectInputStream) {
       inputStream.defaultReadObject()
       serializationSupport.deserialize(this)
   }

This combines the calls above, and invokes ``defaultReadObject()``, ``injectTransients()`` and ``checkForNullTransients()`` If you want to exclude any fields, just set ``serializationSupport.excludedFieldNames`` before invoking ``deserialize()``

Not constructed by Guice
~~~~~~~~~~~~~~~~~~~~~~~~

Usually this happens when an object is created by a factory which then supplies Guice-constructed dependencies and some stateful element to the constructor  - this is typical of a situation which Guice ``AssistedInject`` is used.


.. caution:: Some tests failed when using Guice ``AssistedInject`` with Serialization - we avoid using it, and use manually coded factories instead where needed.  To be fair though, we are not completely sure there is a real problem, see `open issue <https://github.com/KrailOrg/krail/issues/705>`_

.. sourcecode:: java
   :caption: Java

    public class MyObjectFactory{

        public MyObjectFactory (String statefulElement, MyNonSerializableDependency dependency){
         //etc
        }

        private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
            inputStream.defaultReadObject();
            this.dependency = serializationSupport.getInjector().getInstance(MyNonSerializableDependency.class);
        }

    }

