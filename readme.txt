The given client server architecture is run as separate java processes.
The MarketPlace class creates the client for sending requests
The four backend components to be implemented are ServerSideSellersInterface, ServerSideBuyersInterface, CustomerDBServer
and ProductDBServer.

The databases used in this setup are sqlite databases.

The components communicate with each other by passing strings which are then parsed to extract information as JSON objects.
The db server sends JSONArrays and the servers send JSONObjects.