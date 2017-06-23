# coding:utf-8
from email.mime.image import MIMEImage
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.header import Header
import mimetypes
import os
import re
import smtplib
import time
import sys

class EmailUtil():

    mail_host = "smtp.163.com"  # 设置服务器
    mail_user = "blazerhehe@163.com"  # 用户名
    mail_pass = "blazer222"  # 口令
    mail_postfix = "163.com"  # 发件箱的后缀
    me = ("%s<%s>") % (Header('布雷泽何', 'utf-8'), mail_user)

    @staticmethod
    def send(to_list, sub, content, filenames = None, retry = None, seconds = None):
        if retry is None :
            return EmailUtil.__send(to_list, sub, content, filenames)
        else :
            if not seconds :
                seconds = 60
            count = 0
            while True :
                if count == retry :
                    return False
                if EmailUtil.__send(to_list, sub, content, filenames) :
                    return True
                else :
                    count += 1
                    time.sleep(seconds)

    @staticmethod
    def __send(to_list, sub, content, filenames = None):
        try:
            message = MIMEMultipart()
            message.attach(MIMEText(content, _subtype = 'html', _charset = 'UTF-8'))
            message["Subject"] = sub
            message["From"] = EmailUtil.me
            message["To"] = ";".join(to_list)
            if filenames is not None :
                for filename in filenames :
                    if os.path.exists(filename):
                        ctype, encoding = mimetypes.guess_type(filename)
                        if ctype is None or encoding is not None:
                            ctype = "application/octet-stream"
                        subtype = ctype.split("/", 1)
                        attachment = MIMEImage((lambda f: (f.read(), f.close()))(open(filename, "rb"))[0], _subtype = subtype)
                        attachment.add_header("Content-Disposition", "attachment", filename = re.findall('\/(\w+\.\w+)$', filename)[0])
                        message.attach(attachment)
            server = smtplib.SMTP()
            server.connect(EmailUtil.mail_host)
            server.login(EmailUtil.mail_user, EmailUtil.mail_pass)
            server.sendmail(EmailUtil.me, to_list, message.as_string())
            server.close()
            return True
        except Exception, e:
            print 'EmailUtil.__send error:', str(e)
        return False

if __name__ == '__main__':
    print '邮件参数一:', sys.argv[1]
    print '邮件参数二:', sys.argv[2]
    print '邮件参数三:', sys.argv[3]
    print '邮件参数四:', sys.argv[4]
    mails = sys.argv[1].split(',')
    title = sys.argv[2]
    content = sys.argv[3]
    files = sys.argv[4].split(',')
    if EmailUtil.send(mails, title, content, files):
        print "发送成功"
    else:
        print "发送失败"

