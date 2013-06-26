package org.infinispan.protostream.event_driven;

/**
 * A generic implementation of MessageFieldAccessor that dynamically handles annotated classes so you do not have to
 * write this entirely boring logic by hand.
 * <p/>
 * An instance of this class should be automatically made available (on the fly) in SerializationContext for all classes
 * that are annotated with @ProtoTag.
 *
 * @author anistor@redhat.com
 */
public class AnnotationDrivenFieldAccessor<T> implements MessageFieldAccessor<T> {

   public AnnotationDrivenFieldAccessor(Class<T> clazz) {
      // TODO scan clazz for ProtoTag annotations and cache that info so createChild/setField/getField can work at light speed (where c = speed of reflection)
   }

   @Override
   public Object createChild(T parentMessage, int fieldIndex, String fieldName) {
      // TODO implement the annotation-driven voodoo
      return null;
   }

   @Override
   public void setField(T message, int fieldIndex, String fieldName, Object value) {
      // TODO implement the annotation-driven voodoo
   }

   @Override
   public Object getField(T message, int fieldIndex, String fieldName) {
      // TODO implement the annotation-driven voodoo
      return null;
   }
}
