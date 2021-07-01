package com.github.avalon.packet.packet.login;

import com.github.avalon.common.data.DataType;
import com.github.avalon.network.ProtocolType;
import com.github.avalon.packet.annotation.PacketRegister;
import com.github.avalon.packet.packet.Packet;
import com.github.avalon.packet.schema.FunctionScheme;
import com.github.avalon.packet.schema.PacketStrategy;
import com.github.avalon.player.PlayerConnection;

/**
 * This packet is sent to client by server as a part of verification process for "original"
 * minecraft clients.
 *
 * <h3>Packet Strategy</h3>
 *
 * <ul>
 *   <li>1. Server identifier.
 *   <li>2. Server's public key.
 *   <li>3. A sequence of random bytes generated by the server.
 * </ul>
 *
 * @version 1.1
 */
@PacketRegister(
    operationCode = 0x01,
    protocolType = ProtocolType.LOGIN,
    direction = PacketRegister.Direction.CLIENT)
public class PacketEncryptionKeyRequest extends Packet<PacketEncryptionKeyRequest> {

  public PacketStrategy strategy =
      new PacketStrategy(
          new FunctionScheme<>(
              DataType.STRING, this::getServerIdentifier, this::setServerIdentifier),
          new FunctionScheme<>(DataType.BYTE_ARRAY, this::getPublicKey, this::setPublicKey),
          new FunctionScheme<>(DataType.BYTE_ARRAY, this::getVerifyToken, this::setVerifyToken));

  private String serverIdentifier;
  private byte[] publicKey;
  private byte[] verifyToken;

  public PacketEncryptionKeyRequest(String serverIdentifier, byte[] publicKey, byte[] verifyToken) {
    this.serverIdentifier = serverIdentifier;
    this.publicKey = publicKey;
    this.verifyToken = verifyToken;
  }

  public PacketEncryptionKeyRequest() {}

  @Override
  public boolean isAsync() {
    return false;
  }

  @Override
  public void handle(
      PlayerConnection connection, PacketEncryptionKeyRequest packetEncryptionKeyRequest) {}

  @Override
  public PacketStrategy getStrategy() {
    return null;
  }

  public String getServerIdentifier() {
    return serverIdentifier;
  }

  public void setServerIdentifier(String serverIdentifier) {
    this.serverIdentifier = serverIdentifier;
  }

  public byte[] getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(byte[] publicKey) {
    this.publicKey = publicKey;
  }

  public byte[] getVerifyToken() {
    return verifyToken;
  }

  public void setVerifyToken(byte[] verifyToken) {
    this.verifyToken = verifyToken;
  }
}
