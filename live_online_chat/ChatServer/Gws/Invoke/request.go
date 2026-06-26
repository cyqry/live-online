package Invoke

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strings"
)

type Request struct {
	server string
	config *Config
}

type Config struct {
	Form          bool
	CustomHeaders map[string]string
	Remote        bool
	Security      bool
}

func (c *Config) GetForm() bool {
	return c.Form
}

func (c *Config) isRemote() bool {
	return c.Remote
}

func SendRequestWithConfigReturnString(server string, method, path string, data interface{}, config *Config) (string, error) {
	if config == nil {
		config = &Config{
			Form:          false,
			Remote:        false,
			CustomHeaders: map[string]string{},
		}
	}

	request := NewRequest(server, config)
	resp, err := request.Do(method, path, data)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		bs, err := io.ReadAll(resp.Body)
		if err != nil {
			return "", fmt.Errorf("请求失败！")
		}
		return "", fmt.Errorf(string(bs))
	}

	bs, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}
	return string(bs), nil
}

func SendRequestWithConfig[T any](server string, method, path string, data interface{}, config *Config) (*T, error) {

	if config == nil {
		config = &Config{
			Form:          false,
			Remote:        false,
			CustomHeaders: map[string]string{},
		}
	}

	request := NewRequest(server, config)
	resp, err := request.Do(method, path, data)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("request failed with status code: %d", resp.StatusCode)
	}

	var responseType T
	err = json.NewDecoder(resp.Body).Decode(&responseType)
	if err != nil {
		return nil, fmt.Errorf("failed to decode response: %v", err)
	}

	return &responseType, nil
}

func SendRequest[T any](server string, method string, path string, data interface{}) (*T, error) {
	return SendRequestWithConfig[T](server, method, path, data, nil)
}

func NewRequest(server string, config *Config) *Request {
	return &Request{
		server: server,
		config: config,
	}
}

func (r *Request) Do(method string, path string, data interface{}) (*http.Response, error) {
	var body io.Reader
	var u *url.URL
	if r.config != nil {
		u = r.buildURL(path, r.config.Security)
	} else {
		u = r.buildURL(path, false)
	}

	method = strings.ToUpper(method)

	if data != nil {
		switch method {
		case "GET":
			u.RawQuery = r.formatDataAsQuery(data)
		case "POST":
			if r.config != nil {
				if r.config.GetForm() {
					body = strings.NewReader(r.formatDataAsQuery(data))
				} else {
					jsonData, err := json.Marshal(data)
					if err != nil {
						return nil, err
					}
					body = bytes.NewBuffer(jsonData)
				}
			}
		default:
			return nil, fmt.Errorf("不支持方法: %s", method)
		}
	}

	req, err := http.NewRequest(method, u.String(), body)
	if err != nil {
		return nil, err
	}

	if r.config != nil {

		if r.config.GetForm() {
			req.Header.Set("Content-Type", "application/x-www-form-urlencoded")
		} else {
			req.Header.Set("Content-Type", "application/json")
		}

		if r.config.Remote {
			req.Header.Set("wckvgeklnfuedrth", "23424d23d2332wedd2ed2323r2434f34q5g333y4t")
		}

		if r.config.CustomHeaders != nil {
			for k, v := range r.config.CustomHeaders {
				req.Header.Set(k, v)
			}
		}
	}
	fmt.Printf("%v %v %v %v %v\n", r.server, method, path, data, r.config)

	tr := &http.Transport{
		//跳过证书验证
		TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
	}
	client := &http.Client{Transport: tr}
	return client.Do(req)
}

func (r *Request) buildURL(path string, security bool) *url.URL {
	scheme := "http"
	if security {
		scheme = "https"
	}
	return &url.URL{
		Scheme: scheme,
		Host:   r.server,
		Path:   path,
	}
}

func (r *Request) formatDataAsQuery(data interface{}) string {
	values := url.Values{}

	switch d := data.(type) {
	case map[string]string:
		for k, v := range d {
			values.Set(k, v)
		}
	case map[string][]string:
		for k, v := range d {
			for _, vv := range v {
				values.Add(k, vv)
			}
		}
	default:
		panic("unsupported data type")
	}

	return values.Encode()
}
