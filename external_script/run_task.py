# coding=utf-8
import urllib
import urllib2
import cookielib
import requests
import datetime
import json
import sys
reload(sys)
sys.setdefaultencoding('utf8')

test=False

user_service_login_url='http://us.xiwanglife.com/userservice/login.do'
data_service_add_task='http://ds.idc.xiwanglife.com/view/addTask.do'

if test:
    user_service_login_url='http://test.blazerhe.org:8030/userservice/login.do'
    data_service_add_task='http://test.blazerhe.org:8010/view/addTask.do'

'''
支持shell调用data-service接口
Usage: python run_task.py ${username} ${password} ${configId} [emails=${emails}] [params=${params} ... ]
'''
if __name__ == '__main__' :
    headers = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.8',
        'Connection': 'keep-alive',
        'Content-Type': 'application/x-www-form-urlencoded',
        'User-Agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36',
    }
    username = ''
    password = ''
    config_id = ''
    if len(sys.argv) == 1 :
        print 'Usage: python run_task.py ${username} ${password} ${configId} [emails=${emails}] [params=${params} ... ]'
        exit(-1)
    elif len(sys.argv) >= 4 :
        username = str(sys.argv[1])
        password = str(sys.argv[2])
        config_id = str(sys.argv[3])
    params = { }
    if len(sys.argv) > 3:
        count = 0
        for i in sys.argv:
            if count < 3:
                count += 1
                continue
            strs = str(i).split("=")
            if len(strs) == 2 :
                params[strs[0]] = strs[1]
    params['config_id'] = config_id
    print '调用data-service参数:', params
    # 将cookies绑定到一个opener cookie由cookielib自动管理
    cookie = cookielib.CookieJar()
    handler = urllib2.HTTPCookieProcessor(cookie)
    opener = urllib2.build_opener(handler)
    postData = { 'userName' : username , 'password' : password }
    try :
        request = urllib2.Request(user_service_login_url, urllib.urlencode(postData), headers)
        response = opener.open(request)
        rst = json.loads(response.read())
        print str(rst['message'])
        if int(rst['status']) == 201:
            print '程序退出 -1'
            exit(-1)
    except Exception, e:
        print str(e)
        print '程序退出 -1'
        exit(-1)
    
    try :
        request = urllib2.Request(data_service_add_task, urllib.urlencode(params), headers)
        response = opener.open(request)
        rst = json.loads(response.read())
        print str(rst['message'])
        if int(rst['status']) == 300:
            print '程序退出 -1'
            exit(-1)
        else :
            print '调用成功！'
    except Exception, e:
        print str(e)
        exit(-1)

