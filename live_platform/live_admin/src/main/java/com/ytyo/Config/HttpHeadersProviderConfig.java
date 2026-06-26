package com.ytyo.Config;

import com.ytyo.Const.GeneralConst;
import com.ytyo.Const.RemoteConst;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class HttpHeadersProviderConfig implements HttpHeadersProvider {

    @Override
    public HttpHeaders getHeaders(Instance instance) {
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置约定好的请求头参数
        httpHeaders.add(GeneralConst.X_ADMIN_SERVER_HEADER, GeneralConst.X_ADMIN_SERVER_TOKEN);
        return httpHeaders;
    }
}
