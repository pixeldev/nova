package ml.stargirls.nova.paper.player.server;

import com.google.gson.JsonObject;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonReader;
import ml.stargirls.maia.paper.codec.json.MinecraftJsonWriter;
import ml.stargirls.storage.codec.ModelCodec;
import ml.stargirls.storage.util.Validate;

import java.util.UUID;

public class PlayerServerModelCodec {

	public static final ModelCodec.Writer<PlayerServerModel, JsonObject>
		WRITER =
		object -> MinecraftJsonWriter
			          .create()
			          .writeDetailedUuid(
				          "uuid",
				          object.getUuid()
			          )
			          .writeString(
				          "name",
				          object.getName()
			          )
			          .writeString(
				          "server",
				          object.getServerId()
			          )
			          .end();

	public static final ModelCodec.Reader<PlayerServerModel, JsonObject,
		                                     MinecraftJsonReader>
		READER =
		reader -> {
			UUID uuid = Validate.notNull(reader.readDetailedUuid("uuid"));
			String name = Validate.notNull(reader.readString("name"));
			String serverId = Validate.notNull(reader.readString("server"));
			return new PlayerServerModel(
				uuid,
				name,
				serverId
			);
		};
}
