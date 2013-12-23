// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.net;

import jodd.http.HttpConnection;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpRequest;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Socket factory for HTTP proxy.
 */
public class SocketHttpConnectionProvider implements HttpConnectionProvider {

	protected ProxyInfo proxy = ProxyInfo.directProxy();

	/**
	 * Defines proxy to use for created sockets.
	 */
	public void useProxy(ProxyInfo proxyInfo) {
		proxy = proxyInfo;
	}

	/**
	 * Creates new connection from current {@link jodd.http.HttpRequest request}.
	 *
	 * @see #createSocket(javax.net.SocketFactory, String, int)
	 */
	public HttpConnection createHttpConnection(HttpRequest httpRequest) throws IOException {
		Socket socket;

		if (httpRequest.protocol().equals("https")) {
			SSLSocket sslSocket = (SSLSocket) createSocket(
					SSLSocketFactory.getDefault(), httpRequest.host(), 443);

			sslSocket.startHandshake();

			socket = sslSocket;
		} else {
			SocketFactory socketFactory = proxy.getSocketFactory();

			socket = createSocket(socketFactory, httpRequest.host(), httpRequest.port());
		}

		return new SocketHttpConnection(socket);
	}

	/**
	 * Creates a socket with provided socket factory.
	 */
	protected Socket createSocket(SocketFactory socketFactory, String host, int port) throws IOException {
		return socketFactory.createSocket(host, port);
	}

}