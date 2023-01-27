package ml.stargirls.nova.paper.location;

import com.google.gson.JsonObject;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.storage.codec.ModelCodec;
import ml.stargirls.storage.gson.codec.JsonWriter;
import ml.stargirls.storage.mongo.codec.DocumentWriter;
import ml.stargirls.storage.util.Validate;
import org.bson.Document;

public interface ServerLocationModelCodec {

	ModelCodec.Writer<ServerLocationModel, JsonObject> JSON_WRITER =
		(object) -> JsonWriter
			            .create()
			            .writeString("serverId", object.serverId())
			            .writeObject("location", object.location(), LocationModelCodec.JSON_WRITER)
			            .end();

	ModelCodec.Reader<ServerLocationModel, JsonObject, MinecraftJsonReader>
		JSON_READER = reader -> {
		String serverId = Validate.notNull(reader.readString("serverId"), "serverId");
		LocationModel location = Validate.notNull(
			reader.readObject(
				"location",
				LocationModelCodec.JSON_READER),
			"location");
		return new ServerLocationModel(serverId, location);
	};

	ModelCodec.Writer<ServerLocationModel, Document> DOCUMENT_WRITER =
		(object) -> DocumentWriter
			            .create()
			            .writeString("serverId", object.serverId())
			            .writeObject("location", object.location(), LocationModelCodec.DOCUMENT_WRITER)
			            .end();

	ModelCodec.Reader<ServerLocationModel, Document,
		                 MinecraftDocumentReader>
		DOCUMENT_READER = reader -> {
		String serverId = Validate.notNull(reader.readString("serverId"), "serverId");
		LocationModel location = Validate.notNull(
			reader.readObject(
				"location",
				LocationModelCodec.DOCUMENT_READER),
			"location");
		return new ServerLocationModel(serverId, location);
	};
}
