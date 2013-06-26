package org.infinispan.protostream.event_driven;

/**
 * This is another alternative to the protostream approach. While the protostream approach is based on pulling field
 * data out of a stream of tags (which can be a bit inefficient because look-ahead might be needed due to lack of
 * definite field ordering in protobuf and even worse because of repeatable fields being intermingled with other
 * fields), this approach is based on field push (ie. event-driven). If you're an XML guru then you will recognize this
 * approach being much like SAX and the protostream approach being much like DOM.
 * <p/>
 * When unmarshalling, a library-provided stream parser is supposed to scan the tag stream and invoke the registered
 * MessageFieldAccessors (implemented by user or generic ones like AnnotationDrivenFieldAccessor) to create the message
 * tree and set the fields.
 * <p/>
 * When marshalling, the library uses the protobuf descriptor to figure out what field data it has to extract from
 * user's domain objects using getField(..) and then write it to the tag stream.
 * <p/>
 * Implementing MessageFieldAccessors is not the most exciting thing to do (see UserFieldAccessor) and should probably
 * be done only for handling strange cases. For normal usage I would suggest creating a generic
 * AnnotationDrivenMessageFieldAccessor that infers what to do based on the presence of a @ProtoTag annotation on the
 * user's domain object setters and getters. The @ProtoTag annotation of a field could have an optional ValueConverter
 * parameter that is used whenever the field type declared in protofile needs conversion before setting it into the
 * domain model (a common example is a java.util.Date that is usually represented in protofile using an int32 due to
 * lack of date type support in protobuf).
 * <p/>
 * Boring. Only robots are allowed to implement this interface!
 *
 * @author anistor@redhat.com
 * @see ProtoTag
 * @see ValueConverter
 * @see AnnotationDrivenFieldAccessor
 * @see org.infinispan.protostream.Message
 * @see org.infinispan.protostream.UnknownFieldSet
 */
public interface MessageFieldAccessor<T> {

   Object createChild(T parentMessage, int fieldIndex, String fieldName);

   void setField(T message, int fieldIndex, String fieldName, Object value);

   Object getField(T message, int fieldIndex, String fieldName);
}
