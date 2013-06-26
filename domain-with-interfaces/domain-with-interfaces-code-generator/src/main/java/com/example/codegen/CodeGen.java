package com.example.codegen;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.protobuf.Descriptors.FileDescriptor;

// TODO implement extensions registry?, MessageSet support?, packed fields?
// TODO implement default values on reading. can a required field have a default value?

/**
 * @author anistor@redhat.com
 */
public class CodeGen {

   private static final String ENCODER_PACKAGE = "com.example.encoding";

   private static final String[] wireFormatConstantsNames = new String[]{
         "WIRETYPE_VARINT",
         "WIRETYPE_FIXED64",
         "WIRETYPE_LENGTH_DELIMITED",
         "WIRETYPE_START_GROUP",
         "WIRETYPE_END_GROUP",
         "WIRETYPE_FIXED32",
   };

   private static final Map<Descriptors.FieldDescriptor.Type, String> protoTypeToFriendlyName = new HashMap<Descriptors.FieldDescriptor.Type, String>();

   static {
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.DOUBLE, "Double");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.FLOAT, "Float");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.INT64, "Int64");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.UINT64, "UInt64");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.INT32, "Int32");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.FIXED64, "Fixed64");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.FIXED32, "Fixed32");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.BOOL, "Bool");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.STRING, "String");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.GROUP, "Group");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.MESSAGE, "Message");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.BYTES, "Bytes");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.UINT32, "UInt32");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.ENUM, "Enum");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.SFIXED32, "SFixed32");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.SFIXED64, "SFixed64");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.SINT32, "SInt32");
      protoTypeToFriendlyName.put(Descriptors.FieldDescriptor.Type.SINT64, "SInt64");
   }

   private final String outSrcRoot;
   private final String javaPackageOverride;
   private final String javaOuterClassNameOverride;

   public CodeGen(String outSrcRoot, String javaPackageOverride, String javaOuterClassNameOverride) {
      this.outSrcRoot = outSrcRoot;
      this.javaPackageOverride = javaPackageOverride;
      this.javaOuterClassNameOverride = javaOuterClassNameOverride;
   }

   private String generateEnum(Descriptors.EnumDescriptor edp) {
      IndentWriter iw = new IndentWriter();
      boolean isTopLevelClass = edp.getContainingType() == null && !isSingleOutputFile(edp.getFile());
      if (isTopLevelClass) {
         generatePackageStatement(edp.getFile(), iw);
      }
      iw.append("\npublic enum ").append(edp.getName()).append(" {\n");
      iw.inc();
      boolean notFirst = false;
      for (Descriptors.EnumValueDescriptor ev : edp.getValues()) {
         if (notFirst) {
            iw.append(",\n");
         }
         notFirst = true;
         iw.append(ev.getName()).append("(" + ev.getNumber() + ")");
      }
      iw.append(";\n\n");
      iw.append("public final int value;\n\n");
      iw.append("private ").append(edp.getName()).append("(int value) { this.value = value; }\n\n");
      iw.append("public static ").append(edp.getName()).append(" valueOf(int value) {\n");
      iw.inc();
      iw.append("switch (value) {\n");
      iw.inc();
      for (Descriptors.EnumValueDescriptor ev : edp.getValues()) {
         iw.append("case " + ev.getNumber()).append(" : return ").append(ev.getName()).append(";\n");
      }
      iw.append("default : return null; // unknown value, must be written to UnknownFieldSet to avoid data loss!\n");
      iw.dec();
      iw.append("}\n");
      iw.dec();
      iw.append("}\n");
      iw.dec();
      iw.append("}\n");

      if (isTopLevelClass) {
         writeSource(getFullClassName(edp), iw.toString());
      }
      return iw.toString();
   }

   private String generateMessage(Descriptors.Descriptor md) throws IOException {
      IndentWriter iw = new IndentWriter();
      boolean isTopLevelClass = md.getContainingType() == null && !isSingleOutputFile(md.getFile());
      if (isTopLevelClass) {
         generatePackageStatement(md.getFile(), iw);
      }
      iw.append("\n// Message ").append(md.getFullName()).append('\n');
      iw.append("public interface ").append(md.getName()).append(" extends " + ENCODER_PACKAGE + ".Message {\n");
      iw.inc();
      for (Descriptors.Descriptor nestedDescriptor : md.getNestedTypes()) {
         iw.append(generateMessage(nestedDescriptor));
      }
      for (Descriptors.EnumDescriptor edp : md.getEnumTypes()) {
         iw.append(generateEnum(edp));
      }
      for (Descriptors.FieldDescriptor fdf : md.getFields()) {
         generateFieldAccessors(fdf, iw);
      }
      iw.append("\nstatic final " + ENCODER_PACKAGE + ".Encoder<").append(md.getName()).append("> ENCODER = new " + ENCODER_PACKAGE + ".Encoder<").append(md.getName()).append(">() {\n");
      iw.inc();
      for (Descriptors.FieldDescriptor fdf : md.getFields()) {
         iw.append("private static final int ").append(getFieldNumberConstant(fdf)).append(" = " + fdf.getNumber()).append(";\n");
      }
      iw.append("\n@Override\n");
      iw.append("public void encode(com.google.protobuf.CodedOutputStream out, ").append(md.getName()).append(" o) throws java.io.IOException {\n");
      iw.inc();
      generateEncodeMethod(md, iw);
      iw.dec();
      iw.append("}\n\n");
      iw.append("@Override\n");
      iw.append("public ").append(md.getName()).append(" decode(com.google.protobuf.CodedInputStream in, " + ENCODER_PACKAGE + ".MessageFactory mf) throws java.io.IOException {\n");
      iw.inc();
      generateDecodeMethod(md, iw);
      iw.dec();
      iw.append("}\n\n");
      iw.append("@Override\n");
      iw.append("public int computeSize(").append(md.getName()).append(" o) {\n");
      iw.inc();
      generateComputeSizeMethod(md, iw);
      iw.dec();
      iw.append("}\n");
      iw.dec();
      iw.append("};\n");
      iw.dec();
      iw.append("}\n");

      if (isTopLevelClass) {
         writeSource(getFullClassName(md), iw.toString());
      }
      return iw.toString();
   }

   private void generateComputeSizeMethod(Descriptors.Descriptor md, IndentWriter iw) {
      iw.append("int size = 0;\n");
      for (Descriptors.FieldDescriptor fdf : md.getFields()) {
         if (!fdf.isRequired()) {
            iw.append("if (o.").append(createGetterName(fdf.getName())).append("() != null) {\n");
            iw.inc();
         }
         String v;
         if (fdf.isRepeated()) {
            iw.append("for (").append(getJavaType(fdf)).append(" v : o.").append(createGetterName(fdf.getName())).append("()) {\n");
            iw.inc();
            v = "v";
         } else {
            v = "o." + createGetterName(fdf.getName()) + "()";
         }
         switch (fdf.getType()) {
            case DOUBLE:
            case FLOAT:
            case INT64:
            case UINT64:
            case INT32:
            case FIXED64:
            case FIXED32:
            case BOOL:
            case STRING:
            case BYTES:
            case UINT32:
            case SFIXED32:
            case SFIXED64:
            case SINT32:
            case SINT64:
               iw.append("size += com.google.protobuf.CodedOutputStream.compute").append(protoTypeToFriendlyName.get(fdf.getType())).append("Size(").append(getFieldNumberConstant(fdf)).append(", ").append(v).append(");\n");
               break;
            case GROUP:
               iw.append("size += (com.google.protobuf.CodedOutputStream.computeTagSize(").append(getFieldNumberConstant(fdf)).append(") << 1);\n");
               iw.append("size += ").append(getJavaType(fdf)).append(".ENCODER.computeSize(").append(v).append(");\n");
               break;
            case MESSAGE:
               iw.append("{\n");
               iw.inc();
               iw.append("size += com.google.protobuf.CodedOutputStream.computeTagSize(").append(getFieldNumberConstant(fdf)).append(");\n");
               iw.append("int vsize = ").append(getJavaType(fdf)).append(".ENCODER.computeSize(").append(v).append(");\n");
               iw.append("size += (vsize + com.google.protobuf.CodedOutputStream.computeRawVarint32Size(vsize));\n");
               iw.dec();
               iw.append("}\n");
               break;
            case ENUM:
               iw.append("size += com.google.protobuf.CodedOutputStream.computeEnumSize(").append(getFieldNumberConstant(fdf)).append(", ").append(v).append(".value);\n");
               break;
            default:
               throw new IllegalStateException("Unknown field type " + fdf.getType());
         }
         if (fdf.isRepeated()) {
            iw.dec();
            iw.append("}\n");
         }
         if (!fdf.isRequired()) {
            iw.dec();
            iw.append("}\n");
         }
      }
      iw.append("if (o.getUnknownFieldSet() != null) {\n");
      iw.inc();
      iw.append("size += o.getUnknownFieldSet().getSerializedSize();\n");
      iw.dec();
      iw.append("}\n");
      iw.append("return size;\n");
   }

   private void generateDecodeMethod(Descriptors.Descriptor md, IndentWriter iw) {
      int requiredFields = 0;
      for (Descriptors.FieldDescriptor fdf : md.getFields()) {
         if (fdf.isRequired()) {
            requiredFields++;
            iw.append("boolean ").append(fdf.getName()).append("WasSet = false;\n");
         }
      }
      iw.append(md.getName()).append(" o = mf.createInstance(").append(md.getName()).append(".class);\n");
      iw.append("boolean done = false;\n");
      iw.append("while (!done) {\n");
      iw.inc();
      iw.append("int tag = in.readTag();\n");
      iw.append("switch (tag) {\n");
      iw.inc();
      iw.append("case 0:\n");
      iw.inc();
      iw.append("{\n");
      iw.inc();
      iw.append("done = true;\n");
      iw.dec();
      iw.append("}\n");
      iw.append("break;\n");
      iw.dec();
      for (Descriptors.FieldDescriptor fdf : md.getFields()) {
         iw.append("case ").append(getFieldTag(fdf)).append(":\n");
         iw.inc();
         switch (fdf.getType()) {
            case DOUBLE:
            case FLOAT:
            case INT64:
            case UINT64:
            case INT32:
            case FIXED64:
            case FIXED32:
            case BOOL:
            case STRING:
            case BYTES:
            case UINT32:
            case SFIXED32:
            case SFIXED64:
            case SINT32:
            case SINT64:
               iw.append("{\n");
               iw.inc();
               iw.append(getJavaType(fdf)).append(" v = in.read").append(protoTypeToFriendlyName.get(fdf.getType())).append("();\n");
               genSetField(fdf, iw);
               iw.dec();
               iw.append("}\n");
               break;
            case GROUP:
               iw.append("{\n");
               iw.inc();
               iw.append(getJavaType(fdf)).append(" v = ").append(getJavaType(fdf)).append(".ENCODER.decode(in, mf);\n");
               iw.append("in.checkLastTagWas(").append(getFieldEndGroupTag(fdf)).append(");\n");
               genSetField(fdf, iw);
               iw.dec();
               iw.append("}\n");
               break;
            case MESSAGE:
               iw.append("{\n");
               iw.inc();
               iw.append("int length = in.readRawVarint32();\n");
               iw.append("int oldLimit = in.pushLimit(length);\n");
               iw.append(getJavaType(fdf)).append(" v = ").append(getJavaType(fdf)).append(".ENCODER.decode(in, mf);\n");
               iw.append("in.checkLastTagWas(0);\n");
               iw.append("in.popLimit(oldLimit);\n");
               genSetField(fdf, iw);
               iw.dec();
               iw.append("}\n");
               break;
            case ENUM:
               iw.append("{\n");
               iw.inc();
               iw.append("int enumVal = in.readEnum();\n");
               iw.append(getFullClassName(fdf.getEnumType())).append(" v = ").append(getFullClassName(fdf.getEnumType())).append(".valueOf(enumVal);\n");
               iw.append("if (v == null) {\n");
               iw.inc();
               iw.append("if (o.getUnknownFieldSet() == null) { o.setUnknownFieldSet(com.google.protobuf.UnknownFieldSet.getDefaultInstance()); }\n");
               iw.append("o.getUnknownFieldSet().toBuilder().mergeVarintField(").append(getFieldNumberConstant(fdf)).append(", enumVal").append(");\n");
               iw.dec();
               iw.append("} else {\n");
               iw.inc();
               genSetField(fdf, iw);
               iw.dec();
               iw.append("}\n");
               iw.dec();
               iw.append("}\n");
               break;
            default:
               throw new IllegalStateException("Unknown field type " + fdf.getType());
         }
         iw.append("break;\n");
         iw.dec();
      }
      iw.append("default:\n");
      iw.inc();
      iw.append("{\n");
      iw.inc();
      iw.append("com.google.protobuf.UnknownFieldSet.Builder ufsb = o.getUnknownFieldSet() == null ?\n");
      iw.append("\tcom.google.protobuf.UnknownFieldSet.newBuilder() : o.getUnknownFieldSet().toBuilder();\n");
      iw.append("if (!ufsb.mergeFieldFrom(tag, in)) {\n");
      iw.inc();
      iw.append("done = true;\n");
      iw.dec();
      iw.append("}\n");
      iw.append("o.setUnknownFieldSet(ufsb.build());\n");
      iw.dec();
      iw.append("}\n");
      iw.dec();
      iw.dec();
      iw.append("}\n");
      iw.dec();
      iw.append("}\n");
      if (requiredFields > 0) {
         iw.append("\njava.util.List<String> missingFields = new java.util.ArrayList<String>(" + requiredFields + ");\n");
         for (Descriptors.FieldDescriptor fdf : md.getFields()) {
            if (fdf.isRequired()) {
               iw.append("if (!").append(fdf.getName()).append("WasSet) missingFields.add(\"").append(fdf.getName()).append("\");\n");
            }
         }
         iw.append("if (!missingFields.isEmpty()) throw new com.google.protobuf.UninitializedMessageException(missingFields);\n");
      }
      iw.append("return o;\n");
   }

   private String getFieldTag(Descriptors.FieldDescriptor fdf) {
      return getFieldNumberConstant(fdf) + " << 3 | com.google.protobuf.WireFormat." + wireFormatConstantsNames[fdf.getLiteType().getWireType()];
   }

   private String getFieldEndGroupTag(Descriptors.FieldDescriptor fdf) {
      return getFieldNumberConstant(fdf) + " << 3 | com.google.protobuf.WireFormat.WIRETYPE_END_GROUP";
   }

   private String getFieldNumberConstant(Descriptors.FieldDescriptor fdf) {
      return "FIELD_" + fdf.getName().toUpperCase();
   }

   private void genSetField(Descriptors.FieldDescriptor fdf, IndentWriter iw) {
      if (fdf.isRepeated()) {
         iw.append("if (o.").append(createGetterName(fdf.getName())).append("() == null) { o.").append(createSetterName(fdf.getName())).append("(new java.util.ArrayList<").append(getJavaType(fdf)).append(">()); }\n");
         iw.append("o.").append(createGetterName(fdf.getName())).append("().add(v);\n");
      } else {
         iw.append("o.").append(createSetterName(fdf.getName())).append("(v);\n");
      }
      if (fdf.isRequired()) {
         iw.append(fdf.getName()).append("WasSet = true;\n");
      }
   }

   private void generateEncodeMethod(Descriptors.Descriptor md, IndentWriter iw) {
      for (Descriptors.FieldDescriptor fdf : md.getFields()) {
         if (fdf.isRequired()) {
            if (couldBeNull(fdf)) {
               iw.append("if (o.").append(createGetterName(fdf.getName())).append("() == null) throw new IllegalStateException(\"Required field must not be null : ").append(fdf.getName()).append("\");\n");
            }
         } else {
            iw.append("if (o.").append(createGetterName(fdf.getName())).append("() != null) {\n");
            iw.inc();
         }
         String v;
         if (fdf.isRepeated()) {
            iw.append("for (").append(getJavaType(fdf)).append(" v : o.").append(createGetterName(fdf.getName())).append("()) {\n");
            iw.inc();
            v = "v";
         } else {
            v = "o." + createGetterName(fdf.getName()) + "()";
         }
         switch (fdf.getType()) {
            case DOUBLE:
            case FLOAT:
            case INT64:
            case UINT64:
            case INT32:
            case FIXED64:
            case FIXED32:
            case BOOL:
            case STRING:
            case BYTES:
            case UINT32:
            case SFIXED32:
            case SFIXED64:
            case SINT32:
            case SINT64:
               iw.append("out.write").append(protoTypeToFriendlyName.get(fdf.getType())).append("(").append(getFieldNumberConstant(fdf)).append(", ").append(v).append(");\n");
               break;
            case GROUP:
               iw.append("out.writeTag(").append(getFieldNumberConstant(fdf)).append(", com.google.protobuf.WireFormat.WIRETYPE_START_GROUP);\n");
               iw.append(getJavaType(fdf)).append(".ENCODER.encode(out, ").append(v).append(");\n");
               iw.append("out.writeTag(").append(getFieldNumberConstant(fdf)).append(", com.google.protobuf.WireFormat.WIRETYPE_END_GROUP);\n");
               break;
            case MESSAGE:
               iw.append("out.writeTag(").append(getFieldNumberConstant(fdf)).append(", com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\n");
               iw.append("out.writeRawVarint32(").append(getJavaType(fdf)).append(".ENCODER.computeSize(").append(v).append("));\n");
               iw.append(getJavaType(fdf)).append(".ENCODER.encode(out, ").append(v).append(");\n");
               break;
            case ENUM:
               iw.append("out.writeEnum(").append(getFieldNumberConstant(fdf)).append(", ").append(v).append(".value);\n");
               break;
            default:
               throw new IllegalStateException("Unknown field type " + fdf.getType());
         }
         if (fdf.isRepeated()) {
            iw.dec();
            iw.append("}\n");
         }
         if (!fdf.isRequired()) {
            iw.dec();
            iw.append("}\n");
         }
      }
      iw.append("if (o.getUnknownFieldSet() != null) {\n");
      iw.inc();
      iw.append("o.getUnknownFieldSet().writeTo(out);\n");
      iw.dec();
      iw.append("}\n");
   }

   private void generatePackageStatement(FileDescriptor fd, IndentWriter iw) {
      iw.append("//-------------------------------------- DO NOT EDIT! ---------------------------------------\n");
      iw.append("//--------------------- CODE GENERATED BY " + getClass().getName() + " -----------------------\n\n");
      iw.append("package ").append(getJavaPackage(fd)).append(";\n\n");
   }

   private void generateFieldAccessors(Descriptors.FieldDescriptor fdf, IndentWriter iw) {
      String javaType = getJavaType(fdf);
      if (fdf.isRepeated()) {
//         if (fdf.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
//            javaType = "java.util.List<? extends " + javaType + ">";
//         } else {
         javaType = "java.util.List<" + javaType + ">";
//         }
      }

      DescriptorProtos.FieldDescriptorProto fproto = fdf.toProto();
      iw.append("\n// ").append(fproto.getLabel().toString()).append(' ').append(fproto.getType().toString())
            .append(' ').append(fproto.getName()).append(" = " + fproto.getNumber()).append(";\n");
      if (isIndexed(fdf)) {
         iw.append("// Indexed\n");
      }
      iw.append(javaType).append(' ').append(createGetterName(fdf.getName())).append("();\n");
      iw.append("void ").append(createSetterName(fdf.getName())).append("(").append(javaType).append(' ').append(fdf.getName()).append(");\n");
   }

   //todo also check unknown options
   private boolean isIndexed(Descriptors.FieldDescriptor fdf) {
      for (Descriptors.FieldDescriptor fo : fdf.getOptions().getAllFields().keySet()) {
         if (fo.getFullName().contains("Indexed")) {
            return true;
         }
      }
      return false;
   }

   private String createGetterName(String fieldName) {
      return "get" + createAccessorSuffix(fieldName);
   }

   private String createSetterName(String fieldName) {
      return "set" + createAccessorSuffix(fieldName);
   }

   private static String createAccessorSuffix(String fieldName) {
      // fieldName is never empty
      return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
   }

   private String getJavaType(Descriptors.FieldDescriptor fdf) {
      boolean shouldAllowNull = fdf.isRepeated() || fdf.isOptional();
      switch (fdf.getJavaType()) {
         case INT:
            return shouldAllowNull ? "Integer" : "int";
         case LONG:
            return shouldAllowNull ? "Long" : "long";
         case FLOAT:
            return shouldAllowNull ? "Float" : "float";
         case DOUBLE:
            return shouldAllowNull ? "Double" : "double";
         case BOOLEAN:
            return shouldAllowNull ? "Boolean" : "boolean";
         case STRING:
            return "String";
         case BYTE_STRING:
            return "com.google.protobuf.ByteString";
         case ENUM:
            return getFullClassName(fdf.getEnumType());
         case MESSAGE:
            return getFullClassName(fdf.getMessageType());
         default:
            throw new IllegalArgumentException("Unexpected field type " + fdf.getJavaType());
      }
   }

   private boolean couldBeNull(Descriptors.FieldDescriptor fdf) {
      switch (fdf.getJavaType()) {
         case INT:
         case LONG:
         case FLOAT:
         case DOUBLE:
         case BOOLEAN:
            return fdf.isRepeated() || fdf.isOptional();
         case STRING:
         case BYTE_STRING:
         case ENUM:
         case MESSAGE:
            return true;
         default:
            throw new IllegalArgumentException("Unexpected field type " + fdf.getJavaType());
      }
   }

   private String getFullClassName(Descriptors.Descriptor md) {
      if (md.getContainingType() == null) {
         StringBuilder sb = new StringBuilder();
         String packageName = getJavaPackage(md.getFile());
         if (packageName != null) {
            sb.append(packageName).append('.');
         }
         if (getJavaOuterClassname(md.getFile()) != null) {
            sb.append(getJavaOuterClassname(md.getFile())).append('.');
         }
         sb.append(md.getName());
         return sb.toString();
      } else {
         return getFullClassName(md.getContainingType()) + "." + md.getName();
      }
   }

   private String getFullClassName(Descriptors.EnumDescriptor edp) {
      if (edp.getContainingType() == null) {
         StringBuilder sb = new StringBuilder();
         String packageName = getJavaPackage(edp.getFile());
         if (packageName != null) {
            sb.append(packageName).append('.');
         }
         if (getJavaOuterClassname(edp.getFile()) != null) {
            sb.append(getJavaOuterClassname(edp.getFile())).append('.');
         }
         sb.append(edp.getName());
         return sb.toString();
      } else {
         return getFullClassName(edp.getContainingType()) + "." + edp.getName();
      }
   }

   private String getFullOuterClassName(Descriptors.FileDescriptor fd) {
      if (!isSingleOutputFile(fd)) {
         return null;
      }

      StringBuilder sb = new StringBuilder();
      String packageName = getJavaPackage(fd);
      if (packageName != null) {
         sb.append(packageName).append('.');
      }
      sb.append(getJavaOuterClassname(fd));
      return sb.toString();
   }

   private String getJavaOuterClassname(FileDescriptor fd) {
      if (javaOuterClassNameOverride != null) {
         return javaOuterClassNameOverride;
      }
      if (fd.getOptions().getJavaOuterClassname() != null && !fd.getOptions().getJavaOuterClassname().isEmpty()) {
         return fd.getOptions().getJavaOuterClassname();
      }
      return null;
   }

   private String getJavaPackage(FileDescriptor fd) {
      if (javaPackageOverride != null) {
         return javaPackageOverride;
      }
      if (fd.getOptions().getJavaPackage() != null && !fd.getOptions().getJavaPackage().isEmpty()) {
         return fd.getOptions().getJavaPackage();
      } else if (fd.getPackage() != null && !fd.getPackage().isEmpty()) {
         return fd.getPackage();
      }
      return null;
   }

   private boolean isSingleOutputFile(FileDescriptor fd) {
      return getJavaOuterClassname(fd) != null;
   }

   private void writeSource(String fullClassName, CharSequence source) {
      try {
         File file = new File(outSrcRoot, fullClassName.replace('.', File.separatorChar) + ".java");
         file.getParentFile().mkdirs();
         PrintWriter pw = new PrintWriter(new FileOutputStream(file));
         pw.append(source);
         pw.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private FileDescriptor[] resolveDeps(List<String> dependencyList, Map<String, FileDescriptor> map) {
      List<FileDescriptor> deps = new ArrayList<FileDescriptor>();
      for (String fname : dependencyList) {
         if (map.containsKey(fname)) {
            deps.add(map.get(fname));
         } else if (DescriptorProtos.getDescriptor().getName().equals(fname)) {
            deps.add(DescriptorProtos.getDescriptor());
         }
      }
      return deps.toArray(new FileDescriptor[deps.size()]);
   }

   private void processFile(String fileName) throws Exception {
      FileInputStream input = new FileInputStream(fileName);
      DescriptorProtos.FileDescriptorSet descriptorSet = DescriptorProtos.FileDescriptorSet.parseFrom(input);
      input.close();

      Map<String, FileDescriptor> parsedFileDescriptors = new HashMap<String, FileDescriptor>();
      for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
         FileDescriptor[] deps = resolveDeps(fdp.getDependencyList(), parsedFileDescriptors);
         FileDescriptor fd = FileDescriptor.buildFrom(fdp, deps);
         parsedFileDescriptors.put(fd.getName(), fd);

         IndentWriter iw = null;
         boolean isSingleOutputFile = isSingleOutputFile(fd);
         if (isSingleOutputFile) {
            iw = new IndentWriter();
            generatePackageStatement(fd, iw);
            iw.append("public final class ").append(getJavaOuterClassname(fd)).append(" {\n");
            iw.inc();
         }
         for (Descriptors.EnumDescriptor edp : fd.getEnumTypes()) {
            String src = generateEnum(edp);
            if (isSingleOutputFile) {
               iw.append(src).append('\n');
            }
         }
         for (Descriptors.Descriptor mdp : fd.getMessageTypes()) {
            String src = generateMessage(mdp);
            if (isSingleOutputFile) {
               iw.append(src).append('\n');
            }
         }
         if (isSingleOutputFile) {
            iw.dec();
            iw.append("}\n");
            writeSource(getFullOuterClassName(fd), iw.toString());
         }
      }
   }

   public static void main(String[] args) throws Exception {
      if (args.length == 0) {
         System.err.println("First argument should be the output dir");
         System.exit(1);
      }
      String genSrcRoot = args[0];

      String javaPackageOverride = null;
      String javaOuterClassNameOverride = null;
      int i = 1;
      while (i < args.length) {
         if (args[i].trim().equals("--package")) {
            i++;
            if (i == args.length) break;
            javaPackageOverride = args[i++].trim();
            continue;
         } else if (args[i].trim().startsWith("--package ")) {
            javaPackageOverride = args[i++].substring("--package ".length()).trim();
            continue;
         } else if (args[i].trim().equals("--outerClass")) {
            i++;
            if (i == args.length) break;
            javaOuterClassNameOverride = args[i++].trim();
            continue;
         } else if (args[i].trim().startsWith("--outerClass ")) {
            javaOuterClassNameOverride = args[i++].substring("--outerClass ".length()).trim();
            continue;
         }

         CodeGen gen = new CodeGen(genSrcRoot, javaPackageOverride, javaOuterClassNameOverride);
         gen.processFile(args[i++]);

         javaPackageOverride = null;
         javaOuterClassNameOverride = null;
      }
   }
}
