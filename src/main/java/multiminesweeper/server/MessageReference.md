# Message format reference
The basic format is `type:data` sent over a DataOutputStream.
Each message should be with writeUTF and read with readUTF.

# Types
## `system`
System messages
- `close` The connection is closing
- `clientDisconnect` Some client on the connection has disconnected, so the whole thing is ending
- `badMessage` The client sent a badly formatted message
- `tryFindPartner` The client wants a partner if there is one available.
- ``

## `result`
A result from a previous request
- `true` or `false` for boolean requests.

## `chat`
Chat messages, data is an arbitrary string
## `game`
Game specific messages, TBD
