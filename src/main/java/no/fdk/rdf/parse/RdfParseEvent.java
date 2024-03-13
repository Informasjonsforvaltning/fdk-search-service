/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package no.fdk.rdf.parse;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class RdfParseEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 481287795035291936L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"RdfParseEvent\",\"namespace\":\"no.fdk.rdf.parse\",\"fields\":[{\"name\":\"resourceType\",\"type\":{\"type\":\"enum\",\"name\":\"RdfParseResourceType\",\"symbols\":[\"DATASET\",\"DATA_SERVICE\",\"CONCEPT\",\"INFORMATION_MODEL\",\"SERVICE\",\"EVENT\"]}},{\"name\":\"fdkId\",\"type\":\"string\"},{\"name\":\"data\",\"type\":\"string\"},{\"name\":\"timestamp\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<RdfParseEvent> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<RdfParseEvent> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<RdfParseEvent> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<RdfParseEvent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<RdfParseEvent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this RdfParseEvent to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a RdfParseEvent from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a RdfParseEvent instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static RdfParseEvent fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private no.fdk.rdf.parse.RdfParseResourceType resourceType;
  private java.lang.CharSequence fdkId;
  private java.lang.CharSequence data;
  private long timestamp;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public RdfParseEvent() {}

  /**
   * All-args constructor.
   * @param resourceType The new value for resourceType
   * @param fdkId The new value for fdkId
   * @param data The new value for data
   * @param timestamp The new value for timestamp
   */
  public RdfParseEvent(no.fdk.rdf.parse.RdfParseResourceType resourceType, java.lang.CharSequence fdkId, java.lang.CharSequence data, java.lang.Long timestamp) {
    this.resourceType = resourceType;
    this.fdkId = fdkId;
    this.data = data;
    this.timestamp = timestamp;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return resourceType;
    case 1: return fdkId;
    case 2: return data;
    case 3: return timestamp;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: resourceType = (no.fdk.rdf.parse.RdfParseResourceType)value$; break;
    case 1: fdkId = (java.lang.CharSequence)value$; break;
    case 2: data = (java.lang.CharSequence)value$; break;
    case 3: timestamp = (java.lang.Long)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'resourceType' field.
   * @return The value of the 'resourceType' field.
   */
  public no.fdk.rdf.parse.RdfParseResourceType getResourceType() {
    return resourceType;
  }


  /**
   * Sets the value of the 'resourceType' field.
   * @param value the value to set.
   */
  public void setResourceType(no.fdk.rdf.parse.RdfParseResourceType value) {
    this.resourceType = value;
  }

  /**
   * Gets the value of the 'fdkId' field.
   * @return The value of the 'fdkId' field.
   */
  public java.lang.CharSequence getFdkId() {
    return fdkId;
  }


  /**
   * Sets the value of the 'fdkId' field.
   * @param value the value to set.
   */
  public void setFdkId(java.lang.CharSequence value) {
    this.fdkId = value;
  }

  /**
   * Gets the value of the 'data' field.
   * @return The value of the 'data' field.
   */
  public java.lang.CharSequence getData() {
    return data;
  }


  /**
   * Sets the value of the 'data' field.
   * @param value the value to set.
   */
  public void setData(java.lang.CharSequence value) {
    this.data = value;
  }

  /**
   * Gets the value of the 'timestamp' field.
   * @return The value of the 'timestamp' field.
   */
  public long getTimestamp() {
    return timestamp;
  }


  /**
   * Sets the value of the 'timestamp' field.
   * @param value the value to set.
   */
  public void setTimestamp(long value) {
    this.timestamp = value;
  }

  /**
   * Creates a new RdfParseEvent RecordBuilder.
   * @return A new RdfParseEvent RecordBuilder
   */
  public static no.fdk.rdf.parse.RdfParseEvent.Builder newBuilder() {
    return new no.fdk.rdf.parse.RdfParseEvent.Builder();
  }

  /**
   * Creates a new RdfParseEvent RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new RdfParseEvent RecordBuilder
   */
  public static no.fdk.rdf.parse.RdfParseEvent.Builder newBuilder(no.fdk.rdf.parse.RdfParseEvent.Builder other) {
    if (other == null) {
      return new no.fdk.rdf.parse.RdfParseEvent.Builder();
    } else {
      return new no.fdk.rdf.parse.RdfParseEvent.Builder(other);
    }
  }

  /**
   * Creates a new RdfParseEvent RecordBuilder by copying an existing RdfParseEvent instance.
   * @param other The existing instance to copy.
   * @return A new RdfParseEvent RecordBuilder
   */
  public static no.fdk.rdf.parse.RdfParseEvent.Builder newBuilder(no.fdk.rdf.parse.RdfParseEvent other) {
    if (other == null) {
      return new no.fdk.rdf.parse.RdfParseEvent.Builder();
    } else {
      return new no.fdk.rdf.parse.RdfParseEvent.Builder(other);
    }
  }

  /**
   * RecordBuilder for RdfParseEvent instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<RdfParseEvent>
    implements org.apache.avro.data.RecordBuilder<RdfParseEvent> {

    private no.fdk.rdf.parse.RdfParseResourceType resourceType;
    private java.lang.CharSequence fdkId;
    private java.lang.CharSequence data;
    private long timestamp;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(no.fdk.rdf.parse.RdfParseEvent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.resourceType)) {
        this.resourceType = data().deepCopy(fields()[0].schema(), other.resourceType);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.fdkId)) {
        this.fdkId = data().deepCopy(fields()[1].schema(), other.fdkId);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.data)) {
        this.data = data().deepCopy(fields()[2].schema(), other.data);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[3].schema(), other.timestamp);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
    }

    /**
     * Creates a Builder by copying an existing RdfParseEvent instance
     * @param other The existing instance to copy.
     */
    private Builder(no.fdk.rdf.parse.RdfParseEvent other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.resourceType)) {
        this.resourceType = data().deepCopy(fields()[0].schema(), other.resourceType);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.fdkId)) {
        this.fdkId = data().deepCopy(fields()[1].schema(), other.fdkId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.data)) {
        this.data = data().deepCopy(fields()[2].schema(), other.data);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[3].schema(), other.timestamp);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'resourceType' field.
      * @return The value.
      */
    public no.fdk.rdf.parse.RdfParseResourceType getResourceType() {
      return resourceType;
    }


    /**
      * Sets the value of the 'resourceType' field.
      * @param value The value of 'resourceType'.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder setResourceType(no.fdk.rdf.parse.RdfParseResourceType value) {
      validate(fields()[0], value);
      this.resourceType = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'resourceType' field has been set.
      * @return True if the 'resourceType' field has been set, false otherwise.
      */
    public boolean hasResourceType() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'resourceType' field.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder clearResourceType() {
      resourceType = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'fdkId' field.
      * @return The value.
      */
    public java.lang.CharSequence getFdkId() {
      return fdkId;
    }


    /**
      * Sets the value of the 'fdkId' field.
      * @param value The value of 'fdkId'.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder setFdkId(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.fdkId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'fdkId' field has been set.
      * @return True if the 'fdkId' field has been set, false otherwise.
      */
    public boolean hasFdkId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'fdkId' field.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder clearFdkId() {
      fdkId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'data' field.
      * @return The value.
      */
    public java.lang.CharSequence getData() {
      return data;
    }


    /**
      * Sets the value of the 'data' field.
      * @param value The value of 'data'.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder setData(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.data = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'data' field has been set.
      * @return True if the 'data' field has been set, false otherwise.
      */
    public boolean hasData() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'data' field.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder clearData() {
      data = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'timestamp' field.
      * @return The value.
      */
    public long getTimestamp() {
      return timestamp;
    }


    /**
      * Sets the value of the 'timestamp' field.
      * @param value The value of 'timestamp'.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder setTimestamp(long value) {
      validate(fields()[3], value);
      this.timestamp = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'timestamp' field has been set.
      * @return True if the 'timestamp' field has been set, false otherwise.
      */
    public boolean hasTimestamp() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'timestamp' field.
      * @return This builder.
      */
    public no.fdk.rdf.parse.RdfParseEvent.Builder clearTimestamp() {
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RdfParseEvent build() {
      try {
        RdfParseEvent record = new RdfParseEvent();
        record.resourceType = fieldSetFlags()[0] ? this.resourceType : (no.fdk.rdf.parse.RdfParseResourceType) defaultValue(fields()[0]);
        record.fdkId = fieldSetFlags()[1] ? this.fdkId : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.data = fieldSetFlags()[2] ? this.data : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.timestamp = fieldSetFlags()[3] ? this.timestamp : (java.lang.Long) defaultValue(fields()[3]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<RdfParseEvent>
    WRITER$ = (org.apache.avro.io.DatumWriter<RdfParseEvent>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<RdfParseEvent>
    READER$ = (org.apache.avro.io.DatumReader<RdfParseEvent>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeEnum(this.resourceType.ordinal());

    out.writeString(this.fdkId);

    out.writeString(this.data);

    out.writeLong(this.timestamp);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.resourceType = no.fdk.rdf.parse.RdfParseResourceType.values()[in.readEnum()];

      this.fdkId = in.readString(this.fdkId instanceof Utf8 ? (Utf8)this.fdkId : null);

      this.data = in.readString(this.data instanceof Utf8 ? (Utf8)this.data : null);

      this.timestamp = in.readLong();

    } else {
      for (int i = 0; i < 4; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.resourceType = no.fdk.rdf.parse.RdfParseResourceType.values()[in.readEnum()];
          break;

        case 1:
          this.fdkId = in.readString(this.fdkId instanceof Utf8 ? (Utf8)this.fdkId : null);
          break;

        case 2:
          this.data = in.readString(this.data instanceof Utf8 ? (Utf8)this.data : null);
          break;

        case 3:
          this.timestamp = in.readLong();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










