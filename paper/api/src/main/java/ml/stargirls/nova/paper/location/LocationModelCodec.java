package ml.stargirls.nova.paper.location;

import com.google.gson.JsonObject;
import ml.stargirls.maia.paper.codec.document.MinecraftDocumentReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.storage.codec.ModelCodec;
import ml.stargirls.storage.gson.codec.JsonWriter;
import ml.stargirls.storage.mongo.codec.DocumentWriter;
import ml.stargirls.storage.util.Validate;
import org.bson.Document;

public interface LocationModelCodec {

	ModelCodec.Writer<LocationModel, JsonObject> JSON_WRITER =
		object -> JsonWriter.create()
			          .writeString("worldName", object.getWorldName())
			          .writeNumber("x", object.getX())
			          .writeNumber("y", object.getY())
			          .writeNumber("z", object.getZ())
			          .writeNumber("yaw", object.getYaw())
			          .writeNumber("pitch", object.getPitch())
			          .end();

	ModelCodec.Reader<LocationModel, JsonObject, MinecraftJsonReader>
		JSON_READER = reader -> {
		String worldName = Validate.notNull(reader.readString("worldName"), "worldName");
		double x = Validate.notNull(reader.readNumber("x"), "x")
			           .doubleValue();
		double y = Validate.notNull(reader.readNumber("y"), "y")
			           .doubleValue();
		double z = Validate.notNull(reader.readNumber("z"), "z")
			           .doubleValue();
		float yaw = Validate.notNull(reader.readNumber("yaw"), "yaw")
			            .floatValue();
		float pitch = Validate.notNull(reader.readNumber("pitch"), "pitch")
			              .floatValue();
		return LocationModel.create(worldName, x, y, z, yaw, pitch);
	};

	ModelCodec.Writer<LocationModel, Document> DOCUMENT_WRITER =
		object -> DocumentWriter.create()
			          .writeString("worldName", object.getWorldName())
			          .writeNumber("x", object.getX())
			          .writeNumber("y", object.getY())
			          .writeNumber("z", object.getZ())
			          .writeNumber("yaw", object.getYaw())
			          .writeNumber("pitch", object.getPitch())
			          .end();

	ModelCodec.Reader<LocationModel, Document, MinecraftDocumentReader>
		DOCUMENT_READER = reader -> {
		String worldName = Validate.notNull(reader.readString("worldName"), "worldName");
		double x = Validate.notNull(reader.readNumber("x"), "x")
			           .doubleValue();
		double y = Validate.notNull(reader.readNumber("y"), "y")
			           .doubleValue();
		double z = Validate.notNull(reader.readNumber("z"), "z")
			           .doubleValue();
		float yaw = Validate.notNull(reader.readNumber("yaw"), "yaw")
			            .floatValue();
		float pitch = Validate.notNull(reader.readNumber("pitch"), "pitch")
			              .floatValue();
		return LocationModel.create(worldName, x, y, z, yaw, pitch);
	};
}
